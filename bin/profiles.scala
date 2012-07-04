#!/bin/sh
exec bin/scala -nocompdaemon -deprecation -classpath bin -Dfile.encoding=UTF-8 "$0" "$@"
!#

// Local Variables:
// mode: scala
// End:

import sys.process._
import Scripting.shell

val allNames: List[String] =
  shell("""find models/test/benchmarks -name \*.nlogo -maxdepth 1""")
    .map(_.split("/").last.split(" ").head).toList

"mkdir -p tmp/profiles".!

val version =
  shell("""java -classpath target/classes:project/boot/scala-2.9.2/lib/scala-library.jar:resources org.nlogo.headless.Main --fullversion""")
    .next

def benchCommand(name:String) =
  "make bench ARGS=\"" + name + " 60 60\" " +
  "JARGS=-Xrunhprof:cpu=samples,depth=40,file=tmp/profiles/" + name + ".txt"

for(name <- allNames) {
  println(name)
  benchCommand(name).!
}
