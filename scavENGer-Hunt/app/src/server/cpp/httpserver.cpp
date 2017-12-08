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
class SharedBool {
public:
    bool run;
};


void threadProcedure(Socket conn) {

    //read sent message from client
    ByteArray recievedMsg;
    conn.Read(recievedMsg);

    std::string msgString = recievedMsg.ToString();

    std::string header;

    std::string route;

    bool gotHeader = false;

    for (int i = 0; i < msgString.length(); i++) {
        if (msgString[i] == ' ') {
            if (!gotHeader) {
                gotHeader = true;
            } else {
                break;
            }
        } else {
            if (!gotHeader) {
                header.push_back(msgString[i]);
            } else {
                route.push_back(msgString[i]);
            }
        }
    }

    std::cout << "Request type: " << header << "  route: " << route << std::endl;

    //close connection
    conn.Close();
}

void serverThread() {
    //get reference to shared memory
    Shared<SharedBool> sharedMem("run");

    //start server on port 2010
    SocketServer socketServer(2010);

    //vector of created threads
    std::vector<std::thread*> runningThreads;

    //accept connections and respond to them using a new thread
    while (sharedMem->run) {
        Socket conn = socketServer.Accept();
        runningThreads.push_back(new std::thread(threadProcedure, conn));
    }

    //finish up requests
    for (int i = 0; i < runningThreads.size(); i++) {
        (*runningThreads[i]).join();
    }
}

int main(void)
{
    std::cout << "I am a server." << std::endl;

    //get reference to shared memory
    Shared<SharedBool> sharedMem("run", true);

    sharedMem->run = true;

    //create main thread
    std::thread mainThread(serverThread);

    std::cout << "Type anything to quit." << std::endl;

    std::string input;
    std::cin >> input;
    std::cout << "ending..." << std::endl;

    //set shared bool to false and join threads
    sharedMem->run = false;
    mainThread.join();


}