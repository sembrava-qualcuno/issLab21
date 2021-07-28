# ClientApi

All URIs are relative to */sembrava-qualcuno/issLab21*

| Method                                | HTTP request             | Description            |
| ------------------------------------- | ------------------------ | ---------------------- |
| [**carenter**](ClientApi.md#carenter) | **GET** /client/carenter | Enter a car            |
| [**reqenter**](ClientApi.md#reqenter) | **GET** /client/reqenter | Request to enter a car |
| [**reqexit**](ClientApi.md#reqexit)   | **GET** /client/reqexit  | Request to exit a car  |

## **carenter**

Enter a car

### Example

```bash
 carenter  slotnum=value
```

### Parameters

| Name        | Type        | Description             | Notes |
| ----------- | ----------- | ----------------------- | ----- |
| **slotnum** | **integer** | The parking slot number |       |

### Return type

[**TokenId**](TokenId.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **reqenter**

Request to enter a car

The client request to park his/her car

### Example

```bash
 reqenter
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**ParkingSlot**](ParkingSlot.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: application/json

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

## **reqexit**

Request to exit a car

### Example

```bash
 reqexit  tokenid=value
```

### Parameters

| Name        | Type       | Description                            | Notes |
| ----------- | ---------- | -------------------------------------- | ----- |
| **tokenid** | **string** | The client's car corresponding tokenId |       |

### Return type

(empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not Applicable
- **Accept**: Not Applicable

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)
