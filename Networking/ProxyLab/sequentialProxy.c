//
// Created by networking on 11/23/18.
//

#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>

#include "csapp.h"

#include <sys/types.h>
#include <sys/socket.h>
#include <errno.h>
#include <string.h>

#include <netinet/in.h>

#define STR_PROXY_PORT_NUMBER_TO_CLIENT "9002"
#define MAX_NUM_REQUESTS 10
#define MAXLINESIZE 1024
// client info struct holding the ip address and port number
struct ClientInfo {
    struct sockaddr client_address;
    int fdOfPortNum;
};

/* You won't lose style points for including this long line in your code */
const char *user_agent_hdr = "User-Agent: Mozilla/5.0 (X11; Linux x86_64; rv:10.0.3) Gecko/20120305 Firefox/10.0.3\r\n";
const char *connection_hdr = "Connection: close\n";
const char *proxy_conn_hdr = "Proxy-Connection: close\n";


// will hold info for the request queue from client
struct ClientInfoAndRequest {
    struct ClientInfo structClientInfo;
    char client_message[2048];

};

// queue for the getrequestFromClient to place new requests on
// including the client's IP address and port along with the request
static struct ClientInfoAndRequest requestFromClientQueue[MAX_NUM_REQUESTS];

// next request in queue that is to be processed
static int nextRequestFromClientQueue = 0;

// next open slot in queue
static int nextOpenSlotRequestFromClientQueue = 0;

// number of requests in the queue
static int numberOfRequestsInRequestFromClientQueue = 0;

extern int startGetRequestFromClient();

extern int startProcessRequest();

int addClientRequestToRequestFromClientQueue(struct ClientInfoAndRequest structClientInfoAndRequest) {
    if (numberOfRequestsInRequestFromClientQueue >= MAX_NUM_REQUESTS) {
        return -1;
    }
    requestFromClientQueue[nextOpenSlotRequestFromClientQueue] = structClientInfoAndRequest;
    numberOfRequestsInRequestFromClientQueue++;
    return 0;
}

int correctRequestName(struct sockaddr socketAddr, char* requestBuf, char* method, char* url, char* version, char* service) {

    printf("[sequentialProxy - correctRequestName()] Entered correctRequestName()\n");

    // set version number to HTTP/1.0
    strcpy(version, "HTTP/1.0\r\n");

    // If the url has a port number included with url, then separate them into url and service/port for use in the Open_clientfd method
    char* httpStr = "http://";
    char* urlWithoutHttpButWithPort;// = calloc(1, strlen(url) - strlen(httpStr));
    if ((urlWithoutHttpButWithPort = strstr(url, httpStr)) != NULL) {
        strcpy(urlWithoutHttpButWithPort, &strstr(url, httpStr)[strlen(httpStr)]);
        strcpy(service, "80");     // standard port for http protocol
    }

    printf("[sequentialProxy - correctRequestName()] Before the for loop; url: %s, length of url == %lu; urlWithoutHttpButWithPort == %s; length of urlWithoutHttpButWithPort == %lu\n", url, strlen(url), urlWithoutHttpButWithPort, strlen(urlWithoutHttpButWithPort));

    size_t i = 0;
    for (; i < strlen(urlWithoutHttpButWithPort); i++) {
 //       printf("[sequentialProxy - correctRequestName()] Entered the for loop; urlWithoutHttpButWithPort[%lu]: %c;\n", i, urlWithoutHttpButWithPort[i]);

        if (urlWithoutHttpButWithPort[i] == '/') {
            break;
        }
        if (urlWithoutHttpButWithPort[i] == ':') {
            // if the url contained a port number, then put that into service
            if(isdigit(urlWithoutHttpButWithPort[i+1])) {
                // copy the port number to the service char array
                int indexInService = 0;
                for (size_t j = i + 1; j < strlen(urlWithoutHttpButWithPort) && j < i + 6; j++) {
                    if (isdigit(urlWithoutHttpButWithPort[j])) {
                        service[indexInService++] = urlWithoutHttpButWithPort[j];
                    }
                    else {
                        break;
                    }
                }
                break;
            }
            else {
                // means the colon is application specific and does not indicate a port number
                printf("[sequentialProxy - correctRequestName()] PROBLEM with : in url\n");
            }
        }
    }
    char* urlWithoutHTTPnorPort = calloc(1, i);
    strncpy(urlWithoutHTTPnorPort, urlWithoutHttpButWithPort, i);

    printf("[sequentialProxy - correctRequestName()] After the for loop; urlWithoutHTTPnorPort: %s; service == %s\n", urlWithoutHTTPnorPort, service);

     printf("[sequentialProxy - correctRequestName()] urlWithoutHTTPnorPort: %s; url: %s, length of urlWithoutHTTPnorPort == %lu; service == %s\n", urlWithoutHTTPnorPort, url, strlen(urlWithoutHTTPnorPort), service);

    // put the corrected url and version into the requestBuf
    strcpy(requestBuf, method);
    strcat(requestBuf, " ");
    strcat(requestBuf, urlWithoutHTTPnorPort);
    strcat(requestBuf, " ");
    strcat(requestBuf, version);

    strcpy(url, urlWithoutHTTPnorPort);
    printf("[sequentialProxy - correctRequestName()] End of method; urlWithoutHTTPnorPort: %s; url: %s; service == %s\n", urlWithoutHTTPnorPort, url, service);

    return 0;


}

int connectFd(char* urlWithoutHTTPnorPort, char* service) {
    printf("[sequentialProxy - connectFd()] Entered connectFd(%s,%s)\n", urlWithoutHTTPnorPort, service);
    int clientFd = Open_clientfd(urlWithoutHTTPnorPort, service);
    printf("[sequentialProxy - connectFd()] After the Open_clientfd\n");
    if (clientFd < 0) {
        printf("[sequentialProxy - connectFd()] Error: clientfd == -1; urlWithoutHTTPnorPort: %s, length of urlWithoutHTTPnorPort == %lu; service: %s; length of service == %lu\n", urlWithoutHTTPnorPort, strlen(urlWithoutHTTPnorPort), service, strlen(service));
    }

 //   free(service);
  //  free(urlWithoutHTTPnorPort);
    printf("[sequentialProxy - connectFd()] After the Getnameinfo() call; service: %s\n", service);

    return clientFd;
}

int buildUpHeaderBuf(rio_t rio, char* headerBuf, char* url) {
    char tempBuf[MAXBUF];

    printf("[sequentialProxy - buildUpHeaderBuf()] Just before the Rio_readlineb() outside of the while loop \n");
    Rio_readlineb(&rio, headerBuf, MAXLINESIZE);
    if (strstr(headerBuf, "localhost") != NULL) {
        // means that the Host is localhost, which is incorrect, so change the host to being url

        strcpy(headerBuf, "Host: ");
        strcat(headerBuf, url);
        strcat(headerBuf, "\r\n");
    }
    printf("[sequentialProxy - buildUpHeaderBuf()] Just before the while loop headerBuf == %s\n", headerBuf);

    int connectionAdded = 0;
    int proxyConnAdded = 0;
    int userAgentAdded = 0;
    while (((strcasecmp(tempBuf, "\r\n"))!=0) && (rio.rio_cnt > 2)) {
        // modify or put in the connection-hdr thingys from above
        Rio_readlineb(&rio, tempBuf, MAXLINESIZE);
        if (strncmp(tempBuf, "Connection", strlen("Connection")) == 0) {
            strcat(headerBuf, connection_hdr);
            connectionAdded = 1;
            continue;
        }
        if (strncmp(tempBuf, "Proxy-Connection", strlen("Proxy-Connection")) == 0) {
            strcat(headerBuf, proxy_conn_hdr);
            proxyConnAdded = 1;
            continue;
        }
        if (strncmp(tempBuf, "User-Agent", strlen("User-Agent")) == 0) {
            strcat(headerBuf, user_agent_hdr);
            userAgentAdded = 1;
            continue;
        }
        strcat(headerBuf, tempBuf);
        printf("\n\nNext round:\n %s;\n", headerBuf);
        printf("rio_cnt: %d;\n", rio.rio_cnt);
    }
    if (connectionAdded == 0){ strcat(headerBuf, connection_hdr); }
    if (proxyConnAdded == 0) { strcat(headerBuf, proxy_conn_hdr); }
    if (userAgentAdded == 0) { strcat(headerBuf, user_agent_hdr); }
    strcat(headerBuf, "\r\n");
    printf("[sequentialProxy - buildUpHeaderBuf()] Just after the while loop headerBuf == %s\n", headerBuf);

    return 0;
}

int rioReadingOfRequestandHeaderLines(struct ClientInfoAndRequest clientInfoAndRequest, char* url, char* port, char* combinedMessage) {
    rio_t rio;
    char requestBuf[MAXLINESIZE], method[MAXLINESIZE], version[MAXLINESIZE];
    char headerBuf[MAXBUF];
    // initialize the rio read
    Rio_readinitb(&rio, clientInfoAndRequest.structClientInfo.fdOfPortNum);

    // read the request
    Rio_readlineb(&rio, requestBuf, MAXLINESIZE);
    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines(%d)] Request headers:\n", clientInfoAndRequest.structClientInfo.fdOfPortNum);
    sscanf(requestBuf, "%s %s %s", method, url, version);
    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines(%d)] method: %s; url: %s; version: %s:\n", clientInfoAndRequest.structClientInfo.fdOfPortNum, method, url, version);
    if (strcmp(method, "GET") != 0) {
        printf("[sequentialProxy - rioReadingOfRequestandHeaderLines()]Error: can not implement a non-GET method; method was %s;\n", method);
        return -1;
    }

    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines()] About to call correctRequestName\n");
    int err = correctRequestName(clientInfoAndRequest.structClientInfo.client_address, requestBuf, method, url, version, port);
    if (err != 0) {
        printf("[sequentialProxy - rioReadingOfRequestandHeaderLines()] Error: correctRequestName()");
    }

    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines(%d)] requestBuf: %s; url: %s; port == %s\n", clientInfoAndRequest.structClientInfo.fdOfPortNum, requestBuf, url, port);


    // read the headers into a string for use later when send the request to the real website
    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines()] Just before the buildUpHeaderBuf function: headerBuf == %s\n", headerBuf);
    buildUpHeaderBuf(rio, headerBuf, url);
    printf("[sequentialProxy - rioReadingOfRequestandHeaderLines()] Just after the buildUpHeaderBuf function: headerBuf == %s\n", headerBuf);

    strcpy(combinedMessage, requestBuf);
    strcat(combinedMessage, headerBuf);
    // do a rio write of the request line and the headers

    return 0;
}

int connectAndSendAndReceiveMessage(char* url, char* port, char* message, char* response) {
    rio_t rio;
    char tempBuf[MAXLINESIZE];
    printf("[sequentialProxy - connectAndSendAndReceiveMessage()] Just before connectFd(%s, %s, %s, %s)\n", url, port, message, response);

    int clientFd = connectFd(url, port);
    printf("[sequentialProxy - connectAndSendAndReceiveMessage()] After connectFd(); clientFd == %d\n", clientFd);

    Rio_readinitb(&rio, clientFd);
    Rio_writen(clientFd, message, strlen(message));
    printf("[sequentialProxy - connectAndSendAndReceiveMessage()] After Rio_writen(); clientFd == %d; message == %s;\n", clientFd, message);


    Rio_readlineb(&rio, response, MAXLINESIZE);
    while (((strcasecmp(tempBuf, "\r\n"))!=0) && (rio.rio_cnt > 0)) {
        // modify or put in the connection-hdr thingys from above
        Rio_readlineb(&rio, tempBuf, MAXLINESIZE);

        strcat(response, tempBuf);
        printf("\n\nNext round:\n %s;\n", response);
        printf("rio_cnt: %d;\n", rio.rio_cnt);
    }
    printf("[sequentialProxy - connectAndSendAndReceiveMessage()] After Rio_readlineb(); clientFd == %d; response: %s\n", clientFd, response);

    close(clientFd);
    printf("[sequentialProxy - connectAndSendAndReceiveMessage()] After close();\n");

    return 0;
}

int forwardResponseToClient(char* response, int clientFd) {

    Rio_writen(clientFd, response, strlen(response));

    return 0;
}

int getFromClient(int server_socket) {
    char url[MAXLINESIZE], port[MAXLINESIZE], message[MAXBUF], response[MAXBUF];
    printf("[getRequestFromClient - getFromClient(%d)] Entered server_socket\n", server_socket);
    //   char server_message[256] = "You have reached the server!";
    // client's socket that going to be sending the request to the proxy
    struct ClientInfoAndRequest clientInfoAndRequest;// = *(struct ClientInfoAndRequest*) calloc(1, sizeof(struct ClientInfoAndRequest));

    printf("[getRequestFromClient - getFromClient(%d)] After declared the clientInfoAndRequest struct; Before initializing clientInfoAndRequest\n", server_socket);

 /*   clientInfoAndRequest.structClientInfo.client_address.sin_family = AF_INET;
    clientInfoAndRequest.structClientInfo.client_address.sin_port = htons(SERVER_PORT_NUMBER);
    clientInfoAndRequest.structClientInfo.client_address.sin_addr.s_addr = INADDR_ANY;
  */  socklen_t addrLength = sizeof(clientInfoAndRequest.structClientInfo.client_address);
    printf("[getRequestFromClient - getFromClient()] sizeof client address is: %lu\n",sizeof(clientInfoAndRequest.structClientInfo.client_address));
  //  clientInfoAndRequest.structClientInfo.portNum = Accept(server_socket, NULL, NULL);

    clientInfoAndRequest.structClientInfo.fdOfPortNum = Accept(server_socket, &clientInfoAndRequest.structClientInfo.client_address, &addrLength);

    // need to somehow change around the client_address so that the localhost would not be included and instead will start


    if (clientInfoAndRequest.structClientInfo.fdOfPortNum < 0) {
        perror("Accept failed");
        return -1;
        //    strerror(errno);
    }
    printf("[getRequestFromClient - getFromClient(%d)] After called accept() to get the port number: %d; size of client_address: %lu; Before called recv()\n", server_socket, clientInfoAndRequest.structClientInfo.fdOfPortNum, sizeof(clientInfoAndRequest.structClientInfo.client_address));
    // DO NOT NEED EXCEPT TO TEST TO MAKE SURE THAT WE ARE CONNECTED TO THE CLIENT - send the message
    //  send(client_socket, server_message, sizeof(server_message), 0);

    // now we are connected to the client and can accept a request from the client
    // receive request from the client
 //   recv(clientInfoAndRequest.structClientInfo.portNum, &clientInfoAndRequest.client_message, sizeof(clientInfoAndRequest.client_message), 0);
    rioReadingOfRequestandHeaderLines(clientInfoAndRequest, url, port, message);
    //  struct ClientInfo clientInfo = parseClientRequest(client_request);

    connectAndSendAndReceiveMessage(url, port, message, response);
    printf("[getRequestFromClient - getFromClient()] After called connectAndSendAndReceiveMessage(); The response from the server to send to the client is:\n %s\n", response);


    forwardResponseToClient(response, clientInfoAndRequest.structClientInfo.fdOfPortNum);

    printf("[getRequestFromClient - getFromClient(%d)] After forwardResponseToClient()\n", server_socket);

    if (addClientRequestToRequestFromClientQueue(clientInfoAndRequest) == -1) {
        return -1;
    }

    printf("[getRequestFromClient - getFromClient(%d)] End of server_socket; return 0\n", server_socket);

    close(clientInfoAndRequest.structClientInfo.fdOfPortNum);


 //   free(&(clientInfoAndRequest));
    return 0;

}

int startGetRequestFromClient() {
    printf("[getRequestFromClient - startGetRequestFromClient()] sizeof(struct ClientInfoAndRequest) == %ld\n", sizeof(struct ClientInfoAndRequest));


    // create the server socket
    int server_socket;
 //   server_socket = socket(AF_INET, SOCK_STREAM, 0);

    printf("[getRequestFromClient - startGetRequestFromClient()] Before Open_listenfd()\n");
    server_socket = Open_listenfd(STR_PROXY_PORT_NUMBER_TO_CLIENT);

    printf("[getRequestFromClient - startGetRequestFromClient()] after listen(); before getFromClient(server_socket)\n");

    // connect to client and get requests from the client
    //   while(1) {
    getFromClient(server_socket);
    //      close(server_socket);
    // }
    printf("[getRequestFromClient - startGetRequestFromClient()] After called getFromClient; About to call close(server_socket)\n");

    // close the socket
    close(server_socket);

    printf("[getRequestFromClient - startGetRequestFromClient()] After closed the server_socket; about to return 0;\n");

    return 0;
}

int main() {
    // start up all the various parts of the program
    startGetRequestFromClient();

    return 0;
}
