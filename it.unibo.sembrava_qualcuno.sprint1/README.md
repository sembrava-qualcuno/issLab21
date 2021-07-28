# Final Task 2021 Sprint1

This project contains user documentation and code for the first sprint of the Final Task 2021.

The first sprint is relative to the project, testing and deployment of the code for the informIN product owner requirement.

## Automated Car-Parking Bash client

### Overview

This is a Bash client script for accessing Automated Car-Parking service.

The script uses cURL underneath for making all REST calls.

### Usage

```shell
# Make sure the script has executable rights
$ chmod u+x 

# Print the list of operations available on the service
$ ./ -h

# Print the service description
$ ./ --about

# Print detailed information about specific operation
$ ./ <operationId> -h

# Make GET request
./ --host http://<hostname>:<port> --accept xml <operationId> <queryParam1>=<value1> <header_key1>:<header_value2>

# Make GET request using arbitrary curl options (must be passed before <operationId>) to an SSL service using username:password
 -k -sS --tlsv1.2 --host https://<hostname> -u <user>:<password> --accept xml <operationId> <queryParam1>=<value1> <header_key1>:<header_value2>

# Make POST request
$ echo '<body_content>' |  --host <hostname> --content-type json <operationId> -

# Make POST request with simple JSON content, e.g.:
# {
#   "key1": "value1",
#   "key2": "value2",
#   "key3": 23
# }
$ echo '<body_content>' |  --host <hostname> --content-type json <operationId> key1==value1 key2=value2 key3:=23 -

# Preview the cURL command without actually executing it
$  --host http://<hostname>:<port> --dry-run <operationid>
```

### Docker image

You can easily create a Docker image containing a preconfigured environment
for using the REST Bash client including working autocompletion and short
welcome message with basic instructions, using the generated Dockerfile:

```shell
docker build -t my-rest-client .
docker run -it my-rest-client
```

By default you will be logged into a Zsh environment which has much more
advanced auto completion, but you can switch to Bash, where basic autocompletion
is also available.

### Shell completion

#### Bash

The generated bash-completion script can be either directly loaded to the current Bash session using:

```shell
source .bash-completion
```

Alternatively, the script can be copied to the `/etc/bash-completion.d` (or on OSX with Homebrew to `/usr/local/etc/bash-completion.d`):

```shell
sudo cp .bash-completion /etc/bash-completion.d/
```

#### OS X

On OSX you might need to install bash-completion using Homebrew:

```shell
brew install bash-completion
```

and add the following to the `~/.bashrc`:

```shell
if [ -f $(brew --prefix)/etc/bash_completion ]; then
  . $(brew --prefix)/etc/bash_completion
fi
```

#### Zsh

In Zsh, the generated `_` Zsh completion file must be copied to one of the folders under `$FPATH` variable.

### Documentation for API Endpoints

All URIs are relative to */sembrava-qualcuno/issLab21*

| Class            | Method                                                                           | HTTP request                 | Description                                |
| ---------------- | -------------------------------------------------------------------------------- | ---------------------------- | ------------------------------------------ |
| *ClientApi*      | [**carenter**](userDocs/bash-client/docs/ClientApi.md#carenter)                  | **GET** /client/carenter     | Enter a car                                |
| *ClientApi*      | [**reqenter**](userDocs/bash-client/docs/ClientApi.md#reqenter)                  | **GET** /client/reqenter     | Request to enter a car                     |
| *ClientApi*      | [**reqexit**](userDocs/bash-client/docs/ClientApi.md#reqexit)                    | **GET** /client/reqexit      | Request to exit a car                      |
| *ManagerApi*     | [**loginManager**](userDocs/bash-client/docs/ManagerApi.md#loginmanager)         | **POST** /manager/login      | Logs manager into the system               |
| *ManagerApi*     | [**logoutManager**](userDocs/bash-client/docs/ManagerApi.md#logoutmanager)       | **POST** /manager/logout     | Logs out current logged in manager session |
| *ParkingAreaApi* | [**getParkingArea**](userDocs/bash-client/docs/ParkingAreaApi.md#getparkingarea) | **GET** /parkingArea         | Get the parking area state                 |
| *ParkingAreaApi* | [**getTrolley**](userDocs/bash-client/docs/ParkingAreaApi.md#gettrolley)         | **GET** /parkingArea/trolley | Get the trolley state                      |
| *ParkingAreaApi* | [**updateTrolley**](userDocs/bash-client/docs/ParkingAreaApi.md#updatetrolley)   | **PUT** /parkingArea/trolley | Update the current trolley status          |

### Documentation For Models

- [ApiError](userDocs/bash-client/docs/ApiError.md)
- [ParkingArea](userDocs/bash-client/docs/ParkingArea.md)
- [ParkingSlot](userDocs/bash-client/docs/ParkingSlot.md)
- [TokenId](userDocs/bash-client/docs/TokenId.md)
- [Trolley](userDocs/bash-client/docs/Trolley.md)

### Documentation For Authorization

All endpoints do not require authorization.
