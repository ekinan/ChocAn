#Choc An

------------------------------------------------------------------------------------------------------------------
This was my term project for Portland State University's CS 300 course. It
implemented the data processing software for an imaginary organization, 
Chocoholics Anonymous (ChocAn), that helps people (members) overcome chocolate 
addiction. ChocAn has several different providers that each have their own
set of services to help a member out (e.g. a therapy session). Members and
providers are classified by their Member and Provider IDs, respectively.
ChocAn also records some other metadata about both, such as the member and
provider names, addresses, etc.

Every week on Friday, 11:59 PM, four reports are generated for ChocAn. These are:
	1. Member Report for every member
	2. Provider Report for every provider
	3. Electronic funds transfer data (EFT Report)
	4. Summary report to the manager for accounts payable.

The present project implements the functionality to generate these four reports.
In order to test that they work correctly in a real-world setting, a simulated
version of ChocAn was written where the members and providers could either be
loaded from an already-existing file, or created by the user. The simulated
ChocAn lets the user view its members and providers, manually generate Reports
1 and 2, advance the time (to see if the automatic generation works), modify
an existing member or provider's info, add a new member or provider, etc. The
existence of the simulated ChocAn is stored in a directory that can be created
from scratch by the user. The present project already has a pre-loaded one
"ChocAnFiles" that contains some dummy members and providers. To understand
the file structure, please read "File Structure Overview + How To Use Program".

For a more detailed understanding of what the project is implementing, please 
read "Requirements Document".

------------------------------------------------------------------------------------------------------------------
NOTE: Please make sure to run this on a Linux-capable machine, because at the
time I did this project, I hard-coded the directory paths (which can vary
from "\\" for Windows in Java, to "/" in Linux).

To compile the program, please type
	make clean
	make
into the command prompt.

To run the program, please type
	java Main
into the command prompt.

