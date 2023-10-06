.SILENT:
all :	Client

Client:	mainclient.o ../SocketLibrary/SocketLibrary.o moc_windowclient.o windowclient.o
		g++ -Wno-unused-parameter -o Client mainclient.o windowclient.o moc_windowclient.o ../SocketLibrary/SocketLibrary.o  /usr/lib64/libQt5Widgets.so /usr/lib64/libQt5Gui.so /usr/lib64/libQt5Core.so /usr/lib64/libGL.so -lpthread
		echo Création Client

mainclient.o:	mainclient.cpp
		g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o mainclient.o mainclient.cpp
		echo Compilation mainclient

windowclient.o:	windowclient.cpp
		g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I../SocketLibrary -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o windowclient.o windowclient.cpp
		echo Compilation windowclient

moc_windowclient.o:	moc_windowclient.cpp
		g++ -Wno-unused-parameter -c -pipe -g -std=gnu++11 -Wall -W -D_REENTRANT -fPIC -DQT_DEPRECATED_WARNINGS -DQT_QML_DEBUG -DQT_WIDGETS_LIB -DQT_GUI_LIB -DQT_CORE_LIB -I../UNIX_DOSSIER_FINAL -I. -isystem /usr/include/qt5 -isystem /usr/include/qt5/QtWidgets -isystem /usr/include/qt5/QtGui -isystem /usr/include/qt5/QtCore -I. -I. -I/usr/lib64/qt5/mkspecs/linux-g++ -o moc_windowclient.o moc_windowclient.cpp
		echo Compilation moc_windowclient

.SILENT:
clean:
	rm -rf *.o Client
	echo Suppression des fichiers objets et exécutables