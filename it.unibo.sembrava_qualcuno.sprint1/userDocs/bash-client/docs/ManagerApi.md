# ManagerApi

All URIs are relative to */sembrava-qualcuno/issLab21*

Method | HTTP request | Description
------------- | ------------- | -------------
[**loginManager**](ManagerApi.md#loginManager) | **POST** /manager/login | Logs manager into the system
[**logoutManager**](ManagerApi.md#logoutManager) | **POST** /manager/logout | Logs out current logged in manager session


## **loginManager**

Logs manager into the system



### Example
```bash
 loginManager  username=value  password=value
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **username** | **string** | The user name for login |
 **password** | **string** | The password for login in clear text |

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **logoutManager**

Logs out current logged in manager session



### Example
```bash
 logoutManager
```

### Parameters
This endpoint does not need any parameter.

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

