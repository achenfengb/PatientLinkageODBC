Secure Record Linkage
====

Requirement
---
JDK8 must be installed to run this program.

Run the program
---
The two parties are called as geneator and evaluator in garbled circuit protocol. They are using TCP as communication protocol. While then generator is the server and the evaluator is the client. To run the GB protocol, both generator and evaluator need to provide their config files (see the specs of configure file in the following).

To start the run the record linkage program, enter into the program folder, and start one party (generator or evaluator) program:
```
>>java -jar dist/PatientLinkageGC.jar -config <config file>
```

Example
---
The generator's configure file is "config_gen.txt"; the evaluator's configure file is "config_eva_1K.txt".

In the generator side:
```
>>java -jar dist/PatientLinkageGC.jar -config configs/config_gen.txt
```

In the evaluator side:
```
>>java -jar dist/PatientLinkageGC.jar -config configs/config_eva.txt
```

Configure file specs
---
Words after "|" in each line are comments.

**party:**
the role of the program, it can be either “generator” or “evaluator”.

**address:**
the generator address. 

**port:**
the generator port. Note, for multiple threads computation, the same number of consecutive ports starting from this port will be occupied for communications.

**threads:**
computation thread number, and both parties must have the same thread number.

**hash bits:**
the bit number of hashes of the rules.

**results save path:**
the results will be saved is this file.

**id:**
the id property of the database. If it is not specified, the program will create IDs according to the record order in the database.

**rule:**
rule stands for one of the criteria (combination of properties) for matching. For example,  rule "first(12) + last (11) + bdate(8)" contains three properties - first, last and bdate. The number in the bracket is the maximum length of this property. Note, if the bracket includes the letter “S”,  this property will be encoded by soundex method, and the number immediate after S means concatenating with first length of this property. For example, “S3” means encode this property by soundex + the first three character of this property.

**DSN:**
database source name

**DB_name:**
database name

**DB_table:**
table

**DB_user:**
user name

**DB_passwrod:**
passowrd

Contact
---
If you have any question or bug report, feel free to email me at *f4chen@ucsd.edu*.
