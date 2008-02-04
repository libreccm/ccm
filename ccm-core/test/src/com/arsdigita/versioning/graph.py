#!/usr/bin/python2

# Author:  Vadim Nasardinov (vadimn@redhat.com)
# Since:   2003-03-07
# Version: $Id: graph.py 287 2005-02-22 00:29:02Z sskracic $ $Date: 2003/08/01 $

__version__ = "0.02"

'''Extracts pretty-printed graphs from
TEST-com.arsdigita.versioning.XVersioningSuite.txt

See http://www.research.att.com/sw/tools/graphviz/
'''

import sys

class Graph:
    '''A dot graph spec'''

    def __init__(self):
        self._lines = []
        return

    def add_line(self, line):
        self._lines.append(line)
        return

    def __str__(self):
        return "".join(self._lines)


inside = 0
outside = 1

def main():
    assert len(sys.argv) == 2
    junit_report = file(sys.argv[1])

    graphs = []
    state = outside
    for line in junit_report:
        if state == inside:
            graphs[-1].add_line(line)
            if line.startswith("}") and line.strip() == "}":
                state = outside
        else:
            if line.startswith("digraph "):
                graphs.append(Graph())
                graphs[-1].add_line(line)
                state = inside
        
    count = 0
    for graph in graphs:
        dotfile = "graph%d.dot" % count
        pngfile = "graph%d.png" % count

        graph_file = file(dotfile, "w");
        graph_file.write(str(graph))
        graph_file.close()
        print "dot -Tpng -o %s %s; " % (pngfile, dotfile),
        count += 1
    return

if __name__ == '__main__':
    main()
