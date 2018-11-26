# NextClass Server-Side

## Design

### Data Models

Preliminary sample response to a schedule generation request:
```
{
  "courses":[{
    "id": 19,
    "title": "Image Processing & Vision",
    "dept": "CIS",
    "code": "4720",
    "sections": [
      {
        "ref": 1,
        "meetings": [
          {
            // Bitmask encoded days of the week on which
            // the meeting takes place:
            //   Mon Wed Fri 
            // = 1 + 4 + 16 = 21
            "days": 21,
            "type": "LECTURE",
            "timeStart": 1430,
            "timeEnd": 1600
          },
          {
            // Thursday = 8
            "days": 8,
            "type": "LAB",
            "timeStart": 800,
            "timeEnd": 1600
          }
        ]
      },
      {
        "ref": 2,
        ...
      }
      ...
    ]
  },
  {
    ...
  }
  ...
  ],
  
  // These are arrays of section reference codes
  "schedules": [
    [1, 2, 3, 4, 5],
    [1, 9, 3, 4, 5],
    [1, 6, 3, 4, 3],
    ...
    ]
}
```

### Microservices

**Cell**  
Scraper to load WebAdvisor data, demangle it, and upload it into a database.

**Fujita**  
Orchestrator to send scrape requests to the scraper (Cell).

## Resources

### OpenCL
* https://sites.google.com/site/csc8820/opencl-basics/opencl-concepts
* https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/attributes-types.html
* https://www.khronos.org/registry/OpenCL/sdk/1.2/docs/man/xhtml/vectorDataTypes.html
* https://www.khronos.org/registry/OpenCL/specs/2.2/html/OpenCL_Cxx.html#supported-builtin-data-types

### Misc Algorithms
* https://rosettacode.org/wiki/Cartesian_product_of_two_or_more_lists

### Schedule Generation Problem
* https://www.quora.com/What-is-the-fastest-algorithm-to-enumerate-all-maximal-independent-sets-of-a-graph
* https://en.wikipedia.org/wiki/Clique_problem#Finding_maximum_cliques_in_arbitrary_graphs
* The Maximum Clique Problem (Immanuel M. Bomze)

## Notes

### Postgres

**User vs Role:** A role can be a user or a group. CREATE USER is an alias for CREATE ROLE.  
**Public Schema:** Accessible to all users.
