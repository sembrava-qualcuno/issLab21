# ParkingAreaApi

All URIs are relative to */sembrava-qualcuno/issLab21*

Method | HTTP request | Description
------------- | ------------- | -------------
[**getParkingArea**](ParkingAreaApi.md#getParkingArea) | **GET** /parkingArea | Get the parking area state
[**getTrolley**](ParkingAreaApi.md#getTrolley) | **GET** /parkingArea/trolley | Get the trolley state
[**updateTrolley**](ParkingAreaApi.md#updateTrolley) | **PUT** /parkingArea/trolley | Update the current trolley status


## **getParkingArea**

Get the parking area state

An authenticated manager can check out the parking area state

### Example
```bash
 getParkingArea
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**ParkingArea**](ParkingArea.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **getTrolley**

Get the trolley state

An authenticated manager can check out the trolley state

### Example
```bash
 getTrolley
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**Trolley**](Trolley.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not Applicable
 - **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **updateTrolley**

Update the current trolley status

An authenticated manager can start or stop the trolley

### Example
```bash
 updateTrolley
```

### Parameters

Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **body** | [**Trolley**](Trolley.md) | Trolley state that needs to be updated |

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

