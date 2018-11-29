from flask import Flask

app = Flask(__name__)

# The graph needs to tell you if a section collides with another
# We could input a list of section number tuples
class Graph:
    # courses = ( (1, 2, 3, 4, 5), (6, 7), ... )
    def __init__(self, courses):
        pass
    def store(self, a: int, b: int, compatible: bool) -> None:
        pass
    def lookup(self, a: int, b: int) -> bool:
        pass

@app.route("/<semester>/generate")
def generate(semester):
    # Pseudocode

    # Load the sections for each course into memory
    
    # Create a graph data structure
    
    return f"Hello! You're looking at schedules for {semester}."

if __name__ == "__main__":
    app.run(host="0.0.0.0", debug=True, port=11770)
