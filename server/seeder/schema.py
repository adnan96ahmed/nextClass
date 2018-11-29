#!/usr/bin/python
from sqlalchemy import Integer, SmallInteger, String, Boolean, Numeric, Date, Time, Column, ForeignKey, Table
from sqlalchemy.orm import relationship
from sqlalchemy.ext.declarative import declarative_base
Base = declarative_base()

# Association table for ManyToMany Sections and Instructors
section_instructor_assoc = Table('section_instructor_map', Base.metadata,
	Column('section_id', Integer, ForeignKey('section.id'), primary_key = True),
	Column('instructor_id', Integer, ForeignKey('instructor.id'), primary_key = True)
)

class University(Base):
	__tablename__ = 'university'
	id = Column(Integer, primary_key = True)
	name = Column(String(30))
	# Children
	campuses = relationship('Campus')
	departments = relationship('Department')
	semesters = relationship('Semester')
	# Indirect children
	buildings = relationship('Building')
	rooms = relationship('Room')
	instructors = relationship('Instructor')
	courses = relationship('Course')
	sections = relationship('Section')
	meetings = relationship('Meeting')
	meeting_days = relationship('MeetingDay')

class Campus(Base):
	__tablename__ = 'campus'
	id = Column(Integer, primary_key = True)
	name = Column(String(100))
	short_code = Column(String(30))
	# Children
	buildings = relationship('Building')
	# Relationships
	university_id = Column(Integer, ForeignKey('university.id'))
	university = relationship('University', back_populates = 'campuses')

class Building(Base):
	__tablename__ = 'building'
	id = Column(Integer, primary_key = True)
	name = Column(String(100))
	short_code = Column(String(30))
	# Children
	rooms = relationship('Room')
	# Relationships
	campus_id = Column(Integer, ForeignKey('campus.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	campus = relationship('Campus', back_populates = 'buildings')
	university = relationship('University', back_populates = 'buildings')

class Room(Base):
	__tablename__ = 'room'
	id = Column(Integer, primary_key = True)
	name = Column(String(150))
	number = Column(String(30))
	# Children
	meetings = relationship('Meeting')
	# Relationships
	building_id = Column(Integer, ForeignKey('building.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	building = relationship('Building', back_populates = 'rooms')
	university = relationship('University', back_populates = 'rooms')

class Department(Base):
	__tablename__ = 'department'
	id = Column(Integer, primary_key = True)
	name = Column(String(100))
	short_code = Column(String(30))
	# Children
	instructors = relationship('Instructor')
	courses = relationship('Course')
	# Relationships
	university_id = Column(Integer, ForeignKey('university.id'))
	university = relationship('University', back_populates = 'departments')

class Instructor(Base):
	__tablename__ = 'instructor'
	id = Column(Integer, primary_key = True)
	name = Column(String(100))
	short_name = Column(String(50))
	# Special Many-To-Many
	sections = relationship('Section', secondary = section_instructor_assoc, back_populates = 'instructors')
	# Relationships
	department_id = Column(Integer, ForeignKey('department.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	department = relationship('Department', back_populates = 'instructors')
	university = relationship('University', back_populates = 'instructors')

class Semester(Base):
	__tablename__ = 'semester'
	id = Column(Integer, primary_key = True)
	name = Column(String(100))
	code = Column(String(10))
	# Children
	courses = relationship('Course')
	# Relationships
	university_id = Column(Integer, ForeignKey('university.id'))
	university = relationship('University', back_populates = 'semesters')

class Course(Base):
	__tablename__ = 'course'
	id = Column(Integer, primary_key = True)
	name = Column(String(250))
	code = Column(String(10))
	# Children
	sections = relationship('Section')
	# Relationships
	semester_id = Column(Integer, ForeignKey('semester.id'))
	department_id = Column(Integer, ForeignKey('department.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	semester = relationship('Semester', back_populates = 'courses')
	department = relationship('Department', back_populates = 'courses')
	university = relationship('University', back_populates = 'courses')

class Section(Base):
	__tablename__ = 'section'
	id = Column(Integer, primary_key = True)
	local_id = Column(Integer)
	online = Column(Boolean)
	is_open = Column('open', Boolean)
	weight = Column(Numeric)
	remaining_capacity = Column(Integer)
	total_capacity = Column(Integer)
	# Special Many-To-Many
	instructors = relationship('Instructor', secondary = section_instructor_assoc, back_populates = 'sections')
	# Children
	meetings = relationship('Meeting')
	# Relationships
	course_id = Column(Integer, ForeignKey('course.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	course = relationship('Course', back_populates = 'sections')
	university = relationship('University', back_populates = 'sections')

class Meeting(Base):
	__tablename__ = 'meeting'
	id = Column(Integer, primary_key = True)
	start_time = Column(Time)
	end_time = Column(Time)
	start_date = Column(Date)
	end_date = Column(Date)
	meet_type = Column('type', String(30))
	# Children
	meeting_days = relationship('MeetingDay')
	# Relationships
	room_id = Column(Integer, ForeignKey('room.id'))
	section_id = Column(Integer, ForeignKey('section.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	room = relationship('Room', back_populates = 'meetings')
	section = relationship('Section', back_populates = 'meetings')
	university = relationship('University', back_populates = 'meetings')

class MeetingDay(Base):
	__tablename__ = 'meeting_day'
	id = Column(Integer, primary_key = True)
	day_code = Column('day_code', SmallInteger)
	meeting_id = Column(Integer, ForeignKey('meeting.id'))
	university_id = Column(Integer, ForeignKey('university.id'))
	meeting = relationship('Meeting', back_populates = 'meeting_days')
	university = relationship('University', back_populates = 'meeting_days')
