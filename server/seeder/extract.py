#!/usr/bin/python
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from schema import *
from lxml import html
import os, re, json

day_codes = ['Mon', 'Tues', 'Wed', 'Thur', 'Fri', 'Sat', 'Sun']

def index_in_list(search_list, item):
    try:
        return search_list.index(item)
    except:
        return -1

def get_select_options(options):
    opts = {}
    for option in options:
        key = option.xpath('./@value')
        val = option.xpath('./text()')
        # Don't include empty values
        if key and val:
            opts[key[0]] = val[0]
    return opts

def seed(username, password, host, port, database, schema, datapath):
    # S - Sheridan College -> matched out S, Sheridan College
    c_val = re.compile(r'^\w+ - (.*)')
    # AHSS*1020*01 (8401) Human Secur & World Disorder
    # Matches AHSS, 1020, 01, 8401, Human Secur & World Disorder
    course_re = re.compile(r'^(\w+)\*([\dA-Z]+)\*([\dA-Z]+) \((\d+)\) (.*)')
    meeting_re = re.compile(r'^(\d{4}\/\d{2}\/\d{2})-(\d{4}\/\d{2}\/\d{2}) (LEC|LAB|SEM|EXAM|Distance Education) ((?:(?:Mon|Tues|Wed|Thur|Fri|Sat|Sun),? ){1,7}|Days TBA Days TBA, )(?:(\d{2}:\d{2}[AP]M) - (\d{2}:\d{2}[AP]M)|(Times TBA) (Times TBA)), ?(?:([A-Za-z 0-9]+), Room ([0-9A-z/]+)|(Room TBA Room TBA))(?: \(more\)\.\.\.)??$')
    enroll_re = re.compile(r'^(\d+) / (\d+)')

    # Init that sesh
    Session = sessionmaker()
    Session.configure(bind = create_engine(f"postgresql+psycopg2://{username}:{password}@{host}:{port}/{database}"))
    session = Session()
    session.execute(f"SET search_path TO {schema}")

    print("Parsing...")

    # Start the tree with root element (UoG)
    root_uni = University(name = "University of Guelph")

    # Parse out some drop-downs from the search form
    with open(os.path.join(datapath,'wa_form.html'), 'rb') as f:
        tree = html.parse(f)
    semester_opts = get_select_options(tree.xpath('//select[@id="VAR1"]/option'))
    campus_opts = get_select_options(tree.xpath('//select[@id="VAR6"]/option'))
    dept_opts = get_select_options(tree.xpath('//select[@id="LIST_VAR1_1"]/option'))

    # Add the parsed semesters and departments
    for s_code in semester_opts.keys():
        sem = Semester(name = c_val.match(semester_opts[s_code]).group(1), code = s_code)
        root_uni.semesters.append(sem)
    for d_code in dept_opts.keys():
        dept = Department(name = c_val.match(dept_opts[d_code]).group(1), short_code = d_code)
        root_uni.departments.append(dept)

    # Ex. F15, S16, W13
    for c_code in campus_opts.keys():
        curr_campus = Campus(name = c_val.match(campus_opts[c_code]).group(1), short_code = c_code)

        # Loop through the already-added semesters
        for curr_semester in root_uni.semesters:
            # Get the main section table
            with open(os.path.join(datapath, curr_semester.code + "_" + c_code + ".html"), 'rb') as f:
                tree = html.parse(f)
            section_table = tree.xpath('//div[@id="GROUP_Grp_WSS_COURSE_SECTIONS"]/table/tr[position() > 2]')

            # This bit isn't set in stone
            sections = {}
            for section in section_table:
                #0 Semester
                #sections['term'] = section.xpath('./td[2]/div/p/text()')[0]
                #1 Open/Closed
                sections['status'] = section.xpath('./td[3]/div/p/text()')[0]
                #2 Course code & name
                sections['name'] = section.xpath('./td[4]/div/a/text()')[0]
                #3 Campus
                #sections['campus'] = section.xpath('./td[5]/div/p/text()')[0]
                #4 Meeting info
                sections['section'] = section.xpath('./td[6]/div/p/text()')[0]
                #5 Instructor
                sections['instructor'] = section.xpath('./td[7]/div/p/text()')[0]
                #6 Available / Capacity
                sections['capacity'] = section.xpath('./td[8]/div/p/text()')[0]
                #7 Weight
                sections['weight'] = section.xpath('./td[9]/div/p/text()')[0]
                #8 Graduate level
                sections['level'] = section.xpath('./td[11]/div/p/text()')[0]

                # AHSS*1020*01 (8401) Human Secur & World Disorder
                # Matches AHSS, 1020, 01, 8401, Human Secur & World Disorder
                c_grps = course_re.match(sections['name'])
                if c_grps == None:
                    print(sections['name'])
                else:
                    # Get the department object
                    curr_dept = next((d for d in root_uni.departments if d.short_code == c_grps.group(1)), None) 
                    # If the course code has not already been added, add it
                    curr_course = next((c for c in curr_dept.courses if c.code == c_grps.group(2) and c.semester == curr_semester), None)
                    if  curr_course == None:
                        curr_course = Course(name = c_grps.group(5), code = c_grps.group(2),
                            department = curr_dept, semester = curr_semester, university = root_uni)
                        curr_dept.courses.append(curr_course)

                    # Sections can use some boolean(s) cleanup
                    r_cap = None
                    t_cap = None
                    e_grps = enroll_re.match(sections['capacity'])
                    if e_grps:
                        r_cap = e_grps.group(1)
                        t_cap = e_grps.group(2)

                    curr_section = Section(local_id = c_grps.group(4), online = c_grps.group(3) == 'DE',
                     is_open = sections['status'] == 'Open', weight = sections['weight'],
                     remaining_capacity = r_cap, total_capacity = t_cap, course = curr_course, university = root_uni)

                    # Add some instructors
                    for inst_name in sections['instructor'].split(','):
                        inst_name = inst_name.strip()
                        
                        # Storing "TBA TBA" probably ain't necessary
                        if inst_name == 'TBA TBA':
                            continue

                        curr_instructor = next((i for i in curr_dept.instructors if i.short_name == inst_name), None)
                        if curr_instructor == None:
                            curr_instructor = Instructor(short_name = inst_name, university = root_uni)
                        curr_instructor.sections.append(curr_section)
                        curr_dept.instructors.append(curr_instructor)

                    # Time for the tricky bit, adding meetings
                    for line in sections['section'].splitlines():
                        g = meeting_re.match(line)
                        if g:
                            # Add building found in section
                            curr_building = next((b for b in curr_campus.buildings if b.short_code == g.group(9)), None)
                            if curr_building == None:
                                curr_building = Building(short_code = g.group(9), university = root_uni)
                                curr_campus.buildings.append(curr_building)
                            
                            # Add room to building, also found in section
                            curr_room = next((r for r in curr_building.rooms if r.number == g.group(10)), None)
                            if curr_room == None:
                                curr_room = Room(number = g.group(10), university = root_uni)
                                curr_building.rooms.append(curr_room)

                            curr_meeting = Meeting(start_time = g.group(5), end_time = g.group(6),
                                start_date = g.group(1), end_date = g.group(2), meet_type = g.group(3),
                                room = curr_room, section = curr_section, university = root_uni)
                            
                            # Add a 'MeetingDay' entry for every day (ex. Mon, Wed, Fri)
                            for day in g.group(4).split(','):
                                index = index_in_list(day_codes, day.strip())
                                if index > -1:
                                    curr_mday = MeetingDay(day_code = index + 1, university = root_uni)
                                    curr_meeting.meeting_days.append(curr_mday)

        # Lastly, append campus to university
        root_uni.campuses.append(curr_campus)
    print("Adding...")
    session.add(root_uni)
    print("Committing...")
    session.commit()
    print("Done.")
