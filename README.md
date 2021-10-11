# flat-tests
Trasnformation from Canonical XML openEHR COMPOSITIONs to a vendor specific FLAT format

## Build & Run
$ mvn clean install
$ java -Dcompo=path_to_xml_compo -Dopt=path_to_opt -cp target/flat-tests-0.0.1-jar-with-dependencies.jar org.ehrbase.flat_tests.Main

## Sample call
$ java -Dcompo=/home/pablo/Desktop/HiGHmed/minimal_observation.en.v1_20210929041240_000001_1.xml -Dopt=/home/pablo/Desktop/HiGHmed/minimal_observation.opt -cp target/flat-tests-0.0.1-jar-with-dependencies.jar org.ehrbase.flat_tests.Main

## Output
Will generate a file in the same path and with the same name es the -Dcompo value, with the trailing ".flat.json" added to it.

## TODO

- Transform Canonical JSON to FLAT