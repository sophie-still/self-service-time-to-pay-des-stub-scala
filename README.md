# self-service-time-to-pay-des-stub-scala

[![Build Status](https://travis-ci.org/hmrc/self-service-time-to-pay-des-stub-scala.svg)](https://travis-ci.org/hmrc/self-service-time-to-pay-des-stub-scala) [ ![Download](https://api.bintray.com/packages/hmrc/releases/self-service-time-to-pay-des-stub-scala/images/download.svg) ](https://bintray.com/hmrc/releases/self-service-time-to-pay-des-stub-scala/_latestVersion)

This is the stub project for Direct Debit, Arrangement and Eligibility for the Self Service Time To Pay project.

**NOTE: All requests require an Authorization header otherwise a 401 unauthorized request will be sent back**

[Direct Debit](#direct-debit)

[Arrangement](#arrangement)

[Eligibility](#eligibility)

## Direct Debit

There are 2 endpoints available for Direct Debit. 

#### POST   /direct-debits/customers/:credentialId/instructions-request

This requires a credential ID and JSON request. It will return a Direct Debit Instruction.

An example credential ID:

1234567890123456

An example JSON request:

```
{
    "requestingService": "SSTTP",
    "knownFact": [
        {
            "service": "CESA",
            "value": "412345344555"
        },
        {
        "service": "PAYE",
        "value": "872635123"
        }
    ]
}
```

Example JSON responses:

**Success Response - DDI Found**
```
{
  "processingDate": "2001-12-17T09:30:47Z",
  "directDebitInstruction": [
    {
      "sortCode": "123456",
      "accountNumber": "12345678",
      "referenceNumber": "111222333",
      "creationDate": "2001-01-01"
    },
    {
      "sortCode": "654321",
      "accountNumber": "87654321",
      "referenceNumber": "444555666",
      "creationDate": "2001-01-01"
    }
  ]
}
```

**Success Response - No DDIs found**
```
{
  "processingDate": "2001-12-17T09:30:47Z",
  "directDebitInstruction": []
}
```

**Error Response**
```
{
    "reason": "BP not found",
    "reasonCode": "002"
}
```

There are several possible HTTP responses

| Status code | Reason Code | Reason | Meaning | Body |
|---|---|---|---|---|
| 200 | Success | Success | Success response with a list of banks returned | See JSON response example |
| 400 | | Your submission contains one or more errors | Submission has not passed validation | Body will contain the following JSON { "reason" : "Text from reason column", "reasonCode": "Optional code" } |
| 400 | | Invalid JSON message received | Malformed JSON received | |
| 404 | | Resource not found | The remote endpoint has indicated that no data can be found | |
| 404 | 002 | BP not found | Business Partner not found | |
| 500 | | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

NOTE: There are two successful responses that can be obtained - One with a populated list of DirectDebitInstructions and one with an empty list.
To receive a populated list use the example credential ID
To receive an empty list use the credential ID 1234567890


| Status Code           | Required Request Body Changes                           |
|-----------------------|---------------------------------------------------------|
| 200                   | See JSON request example                                |
| 400                   | Remove either requestingService or knownFact from the request body|
| 400                   | "requestingService" : "forceInvalidJSONFormat"                 |
| 404                   | "requestingService" : "force404"                        |
| 404 - 002 Reason Code | Any credential ID that does not match the example above |
| 500                   | "requestingService" : "force500"                        |
| 503                   | "requestingService" : "force503"                        |

#### POST   /direct-debits/customers/:credentialId/instructions/payment-plans

This requires a credential ID and JSON request. It will return a Direct Debit Instruction and Payment Plan

An example credential ID:

1234567890123456

An example JSON request:

```
{
  "requestingService": "SSTTP",
  "submissionDateTime": "2016-07-22T09:30:47Z",
  "knownFact": [
    {
      "service": "CESA",
      "value": "123456678123123"
    },
    {
      "service": "NTC",
      "value": "7263817263321"
    }
  ],
  "directDebitInstruction": {
    "sortCode": "123456",
    "accountNumber": "12345678",
    "accountName": "Current",
    "paperAuddisFlag": false,
    "ddiRefNumber": "1234567890"
  },
  "paymentPlan": {
    "ppType": "Time to Pay",
    "paymentReference": "ABCDEFG12345678901",
    "hodService": "CESA",
    "paymentCurrency": "GBP",
    "initialPaymentAmount": "1024.12",
    "initialPaymentStartDate": "2001-01-01",
    "scheduledPaymentAmount": "2763.23",
    "scheduledPaymentStartDate": "2001-01-01",
    "scheduledPaymentEndDate": "2001-01-01",
    "scheduledPaymentFrequency": "Weekly",
    "balancingPaymentAmount": "102.67",
    "balancingPaymentDate": "2001-01-01",
    "totalLiability": "20123.76",
    "suspensionStartDate": "2001-01-01",
    "suspensionEndDate": "2001-01-01"
  },
  "printFlag": true
}
```

Example JSON responses:

**Success Response**
```
{
  "processingDate": "2001-12-17T09:30:47Z",
  "acknowledgementId": "123456789012345678901234567890123456789012345678901234567890",
  "directDebitInstruction": [
    {
      "ddiReferenceNo": "ABCDabcd1234"
    },
    {
      "ddiReferenceNo": "WXYZwxyz5678"
    }
  ],
  "paymentPlan": [
    {
      "ppReferenceNo": "abcdefghij1234567890"
    },
    {
      "ppReferenceNo": "klmnopqrst0987654321"
    }
  ]
}
```

**Error Response**
```
{
  "reason": "SERVICE missing or invalid",
  "reasonCode": "001"
}
```

There are several possible HTTP responses

| Status code | Reason Code | Reason | Meaning | Body |
|---|---|---|---|---|
| 201 | | Created | The Direct Debit instruction has been Successfully created | See JSON response example |
| 400 | | Your submission contains one or more errors | Submission has not passed validation | Body will contain the following JSON { "reason" : "Text from reason column", "reasonCode": "Optional code" } |
| 400 | | Invalid JSON message received | Malformed JSON received | |
| 400 | 001 | SERVICE missing or invalid | requestingService is missing or invalid | |
| 404 | | Resource not found | The remote endpoint has indicated that no data can be found | |
| 500 | | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

| Status Code           | Required Request Body Changes                           |
|-----------------------|---------------------------------------------------------|
| 201                   | See JSON request example                                |
| 400                   | Remove requestingService from the request body          |
| 400                   | "requestingService" : "forceInvalidJSONFormat"                |
| 400 - 001 Reason Code | "requestingService" : "force400"                        |
| 404                   | "requestingService" : "force404"                        |
| 500                   | "requestingService" : "force500"                        |
| 503                   | "requestingService" : "force503"                        |

## Arrangement

There is 1 endpoint available for Arrangement.

#### POST /time-to-pay/taxpayers/:utr/arrangements

An example UTR is:

1234567890

An example JSON request:

```
{
  "ttpArrangement": {
    "startDate": "2016-08-09",
    "endDate": "2016-09-16",
    "firstPaymentDate": "2016-08-09",
    "firstPaymentAmount": "90000.00",
    "regularPaymentAmount": "6000.00",
    "regularPaymentFrequency": "Monthly",
    "reviewDate": "2016-08-09",
    "initials": "DOM",
    "enforcementAction": "CCP",
    "directDebit": true,
    "debitDetails": [
      {
        "debitType": "IN2",
        "dueDate": "2004-07-31"
      }
    ],
    "saNote": "SA Note Text Here"
  },
  "letterAndControl": {
    "customerName": "Customer Name",
    "salutation": "Dear Sir or Madam",
    "addressLine1": "Plaza 2",
    "addressLine2": "Ironmasters Way",
    "addressLine3": "Telford",
    "addressLine4": "Shropshire",
    "addressLine5": "UK",
    "postCode": "TF3 4NA",
    "totalAll": "50000",
    "clmIndicateInt": "Interest is due",
    "clmPymtString": "1 payment of x.xx then 11 payments of x.xx",
    "officeName1": "office name 1",
    "officeName2": "office name 2",
    "officePostcode": "TF2 8JU",
    "officePhone": "1234567",
    "officeFax": "12345678",
    "officeOpeningHours": "9-5",
    "template": "template",
    "exceptionType": "2",
    "exceptionReason": "Customer requires Large Format printing"
  }
}
```

Example JSON responses:

**Error Response**
```
{
    "reason": "Server error",
    "reasonCode": ""
}
```

There are several possible HTTP responses

| Status code | Reason | Meaning | Body |
|---|---|---|---|
| 202 | Accepted | The request has been accepted for processing, but the processing has not been completed. The request might or might not eventually be acted upon, as it might be disallowed when processing actually takes place. | None |
| 400 | Your submission contains one or more errors | Submission has not passed validation | Body will contain the following JSON { "reason" : "Text from reason column" } |
| 400 | Invalid JSON message received | Malformed JSON received | |
| 500 | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

| Status Code           | Required Request Body Changes                           |
|-----------------------|---------------------------------------------------------|
| 202                   | See JSON request example                                |
| 400                   | Remove ttpArrangement or letterAndControl from the request body |
| 400                   | "enforcementAction" : "forceInvalidJSONFormat"               |
| 500                   | "enforcementAction" : "force500"                        |
| 503                   | "enforcementAction" : "force503"                        |

## Eligibility

There are 3 endpoints available for Eligibility.

#### GET /sa/taxpayer/:utr/returns

An example UTR is:

1234567890

Example JSON responses:

**Success Response**
```
{
  "returns": [
    {
      "taxYearEnd": "2014-04-05",
      "receivedDate": "2014-11-28"
    },
    {
      "taxYearEnd": "2014-04-05",
      "issuedDate": "2015-04-06",
      "dueDate": "2016-01-31"
    },
    {
      "taxYearEnd": "2014-04-05",
      "issuedDate": "2016-04-06",
      "dueDate": "2017-01-31",
      "receivedDate": "2016-04-11"
    }
  ]
}
```

**Error Response**
```
{
  "reason": "Server error",
  "reasonCode": ""
}
```

There are several possible HTTP responses

| Status code | Reason | Meaning | Body |
|---|---|---|---|
| 200 | Success | Success | See JSON response example |
| 404 | Resource not found | The remote endpoint has indicated that no UTR number can be found | No Response body |
| 400 | Your submission contains one or more errors | The request has not passed validation, invalid UTR | Body will contain the following JSON { "reason" : "Text from reason column" } |
| 500 | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

| Status Code           | Required Request UTR Changes                            |
|-----------------------|---------------------------------------------------------|
| 200                   | Use example UTR                                         |
| 404                   | Pass value 0 as UTR                                     |
| 400                   | Use UTR 1234567890Z        |
| 500                   | Pass value force500 as utr                              |
| 503                   | Pass value force503 as utr                              |

#### GET /sa/taxpayer/:utr/debits

An example UTR is:

1234567890

Example JSON responses:

**Success Response**
```
{
  "debits": [
    {
      "taxYearEnd": "2016-04-05",
      "charge": {
        "originCode": "IN1",
        "creationDate": "2015-11-05"
      },
      "relevantDueDate": "2015-11-05",
      "totalOutstanding": 5000,
      "interest": {
        "creationDate": "2015-11-05",
        "amount": 500
      }
    }
  ]
}
```

**Error Response**
```
{
  "reason": "Server error",
  "reasonCode": ""
}
```

There are several possible HTTP responses

| Status code | Reason | Meaning | Body |
|---|---|---|---|
| 200 | Success | Success | See JSON response example |
| 404 | Resource not found | The remote endpoint has indicated that no UTR number can be found | No Response body |
| 400 | Your submission contains one or more errors | The request has not passed validation, invalid UTR | Body will contain the following JSON { "reason" : "Text from reason column" } |
| 500 | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

| Status Code           | Required Request UTR Changes                            |
|-----------------------|---------------------------------------------------------|
| 200                   | Use example UTR                                         |
| 404                   | Pass value 0 as UTR                                     |
| 400                   | Use UTR 1234567890Z       |
| 500                   | Pass value force500 as utr                              |
| 503                   | Pass value force503 as utr                              |

#### GET /sa/taxpayer/:utr/communication-preferences

An example UTR is:

1234567890

Example JSON responses:

**Success Response**
```
{
  "welshLanguageIndicator": true,
  "audioIndicator": false,
  "largePrintIndicator": false,
  "brailleIndicator": false
}
```

**Error Response**
```
{
  "reason": "Server error",
  "reasonCode": ""
}
```

There are several possible HTTP responses

| Status code | Reason | Meaning | Body |
|---|---|---|---|
| 200 | Success | Success | See JSON response example |
| 404 | Resource not found | The remote endpoint has indicated that no UTR number can be found | No Response body |
| 400 | Your submission contains one or more errors | The request has not passed validation, invalid UTR | Body will contain the following JSON { "reason" : "Text from reason column" } |
| 500 | Server error | DES is currently experiencing problems that require live service intervention | |
| 503 | Service unavailable | Dependent systems are currently not responding | |

For the purpose of testing, specific status codes can be triggered via the following methods:

| Status Code           | Required Request UTR Changes                            |
|-----------------------|---------------------------------------------------------|
| 200                   | Use example UTR                                         |
| 404                   | Pass value 0 as UTR                                     |
| 400                   | Use UTR 1234567890Z        |
| 500                   | Pass value force500 as utr                              |
| 503                   | Pass value force503 as utr                              |

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
    