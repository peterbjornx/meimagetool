Intel Management Engine Image Tool
==================================
This set of tools allow to extract and create firmware images for the ME, 
and was created by Peter Bosch for personal use. The code was not written
with quality in mind, and as such there
are little to no comments and no unit tests. As they say at my hackerspace:
 Patches welcome.

The images produced by these  tools are not yet verified against real 
hardware but do load in Intel FIT.
 
Credits
-------

The information required to implement the tool was gathered from original
research and the following resources:
* ME Analyzer (https://github.com/platomav/MEAnalyzer) by Plato Mavropoulos
* unME11 (https://github.com/ptresearch/unME11) by Dmitry Sklyarov (@_Dmit)
* parseMFS (https://github.com/ptresearch/parseMFS) by Dmitry Sklyarov (@_Dmit)
* Intel ME: Flash Filesystem Explained (https://www.blackhat.com/eu-17/briefings.html#intel-me-flash-file-system-explained) by Dmitry Sklyarov (@_Dmit)
* Various information from Igor Skochinsky

It currently consists of three tools:

ME Region Tool
--------------

The ME region tool allows extracting a ME region into its constituent partitions.
It produces an XML file containing the information contained within the Flash 
Partition Table that cannot be inferred from the files.

MFS Tool
--------

The MFS tools allows extracting MFS volumes on the FAT level, MFS directories
are not yet supported. The tool can also add files to volumes, but not overwrite them.

ME Config Tool
--------------
The ME config tool can convert `intel.cfg` and `fitc.cfg` format archives into
their contents and reconstruct them. Metadata and file lists are stored in an
XML archive and as such the tool does not require the host filesystem to support
UNIX permissions.

  
