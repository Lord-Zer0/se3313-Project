#include "thread.h"
#include "socketserver.h"
#include <stdlib.h>
#include <time.h>
#include <list>
#include <vector>
#include <string>
#include <locale>
#include "SharedObject.h"

using namespace Sync;


int main(void)
{


    //start server on port 2010
    SocketServer socketServer(2010);

    while(true) {
        std::cout << "I am a server." << std::endl;

        Socket conn = socketServer.Accept();

        //read sent message from client
        ByteArray recievedMsg;
        conn.Read(recievedMsg);

        std::string msgString = recievedMsg.ToString();

        std::string header;

        for (int i = 0; i < msgString.length(); i++) {
            if (msgString[i] == ' ') {
                break;
            }
            header.push_back(msgString[i]);
        }
        if (header == "GET") {
            std::cout << "This was a GET request." << std::endl;
        }

        std::cout << msgString << std::endl;

        ByteArray response("Hello World");
        conn.Write(response);

        conn.Close();

    }
    
}
