#include "thread.h"
#include "socketserver.h"
#include <stdlib.h>
#include <time.h>
#include <list>
#include <vector>
#include <string>
#include <locale>
#include "SharedObject.h"
#include <iostream>
#include <fstream>
#include <sstream>

using namespace Sync;
class SharedBool {
public:
    bool run;
    ThreadSem currentgames_sem;
    //vector of created threads
    std::vector<std::thread*> runningThreads;
};

void ongoingGameThread(Socket conn) {

}

void threadProcedure(Socket conn) {
    //get reference to shared memory
    Shared<SharedBool> sharedMem("run");

    //read sent message from client
    ByteArray receivedMsg;
    conn.Read(receivedMsg);

    std::string msgString = receivedMsg.ToString();

    std::cout << "received: " << msgString << std::endl;

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
    //GET requests
    if (header == "GET") {
        
        //get game info
        if (route.substr(0, 6) == "/game/") {
            std::string gameID = route.substr(6, 6);
            
            //open file, use semaphore
            sharedMem->currentgames_sem.Wait();

            std::ifstream myfile ("currentgames.txt");


            while (myfile.good()) {
                std::string line;
                getline(myfile, line);

                std::istringstream lineSS(line);

                std::vector<std::string> lineVector;

                while ( lineSS ) {
                    std::string value;

                    if (!getline(lineSS, value, ',')) break;

                    lineVector.push_back(value);
                }

                if (lineVector[0] == gameID) {
                    std::cout << "your game was found: " << gameID << std::endl;

                    std::stringstream response;
                    response << "{ \"ID\": \"" << lineVector[0] << "\", \"Status\": \"" << lineVector[1] <<  "\", \"CurrentWord\": \"" << lineVector[2] << "\" }";
                    
                    ByteArray res (response.str());

                    conn.Write(res);
                    conn.Close();

                    break;
                }
            }
            myfile.close();

            //signal semaphore
            sharedMem->currentgames_sem.Signal();


            //client wants to start a game
        } else if (route.substr(0,11) == "/startgame/") {

             //open file, use semaphore
            sharedMem->currentgames_sem.Wait();

            std::ifstream myfile ("currentgames.txt");

            //check if there are any ongoing games
            while (myfile.good()) {
                std::string line;
                getline(myfile, line);

                std::istringstream lineSS(line);

                std::vector<std::string> lineVector;

                while ( lineSS ) {
                    std::string value;

                    if (!getline(lineSS, value, ',')) break;

                    lineVector.push_back(value);
                }

                if (lineVector[1] == "WAITING") {

                    //send game info
                    std::stringstream response;
                    response << "{ \"ID\": \"" << lineVector[0] << "\", \"Status\": \"" << lineVector[1] <<  "\", \"CurrentWord\": \"" << lineVector[2] << "\" }";
                    
                    ByteArray res (response.str());

                    conn.Write(res);
                    conn.Close();
                    break;
                }
            }
            myfile.close();

            //signal semaphore
            sharedMem->currentgames_sem.Signal();
        
        }
    }
}

void serverThread() {
    //get reference to shared memory
    Shared<SharedBool> sharedMem("run");

    //start server on port 2010
    SocketServer socketServer(2010);

    //accept connections and respond to them using a new thread
    while (sharedMem->run) {
        Socket conn = socketServer.Accept();
        sharedMem->runningThreads.push_back(new std::thread(threadProcedure, conn));
    }

    //finish up requests
    for (int i = 0; i < sharedMem->runningThreads.size(); i++) {
        (*sharedMem->runningThreads[i]).join();
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