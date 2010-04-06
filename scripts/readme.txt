The scripts here are intended to build our product from the command line. Here are two links which I found useful to understand how to build an eclipse based IDE. 

http://knol.google.com/k/automating-eclipse-pde-build
http://help.eclipse.org/ganymede/index.jsp?nav=/4_2_0

shepard.sh starts the automated build process and expects to be called from hudson. build.xml and assocatiated properties files are part of the Plug-in Development Environment (PDE - http://www.eclipse.org/pde/).  Anything that ends with SED is intended to be a template where tokens are replaced with values via the sed program. 

