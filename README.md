# self-service-time-to-pay-des-stub-scala

[![Build Status](https://travis-ci.org/hmrc/self-service-time-to-pay-des-stub-scala.svg)](https://travis-ci.org/hmrc/self-service-time-to-pay-des-stub-scala) [ ![Download](https://api.bintray.com/packages/hmrc/releases/self-service-time-to-pay-des-stub-scala/images/download.svg) ](https://bintray.com/hmrc/releases/self-service-time-to-pay-des-stub-scala/_latestVersion)

This is the stub project for Direct Debit, Arrangement and Eligibility for the Self Service Time To Pay project.

**NOTE: All requests require an Authorization header otherwise a 401 unauthorized request will be sent back**

[Direct Debit](#direct-debit)

[Arrangement](#arrangement)

[Eligibility](#eligibility)

Below is a diagram which shows what the des-stub fills on for, in the SSTTP project.

<a href="https://github.com/hmrc/self-service-time-to-pay-des-stub-scala">
    <p align="center">
      <img src="https://raw.githubusercontent.com/hmrc/self-service-time-to-pay-des-stub-scala/master/public/ServiceOverview.png" alt="ServiceOverview">
    </p>
</a>

### Service Definitions
The default port for this service is 8887.

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

For the purpose of testing, specific status codes can be triggered via the following methods:

NOTE: There are two successful responses that can be obtained - One with a populated list of DirectDebitInstructions and one with an empty list.
To receive a populated list use the example credential ID
To receive an empty list use the credential ID 543212300016


| Status Code           | Required Request Body Changes                           |
|-----------------------|---------------------------------------------------------|
| 200                   | See JSON request example                                |
| 400                   | Remove either requestingService or knownFact from the request body|
| 400                   | "requestingService" : "forceInvalidJSONFormat"                 |
| 404                   | "requestingService" : "force404"                        |
| 404 - 002 Reason Code | Credential ID : 1234567890123456 |
| 500                   | "requestingService" : "force500"                        |

#### POST   /direct-debits/customers/:credentialId/instructions/payment-plans

This requires a credential ID and JSON request. It will return a Direct Debit Instruction and Payment Plan

An example credential ID:

1234567890123456

An example JSON request:

```
{
    "requestingService": "SSTTP",
    "submissionDateTime": "2017-03-06T11:42:47Z",
    "knownFact": [
        {
          "service": "CESA",
          "value": "2435657686"
        }
    ],
    "directDebitInstruction": {
      "paperAuddisFlag": true,
      "ddiRefNumber": "000100012437"
    },
    "paymentPlan": {
      "ppType": "Time to Pay",
      "paymentReference": "2435657686K",
      "hodService": "CESA",
      "paymentCurrency": "GBP",
      "initialPaymentAmount": "100.00",
      "initialPaymentStartDate": "2017-03-13",
      "scheduledPaymentAmount": "612.50",
      "scheduledPaymentStartDate": "2017-03-20",
      "scheduledPaymentEndDate": "2017-10-20",
      "scheduledPaymentFrequency": "Calendar Monthly",
      "balancingPaymentAmount": "619.51",
      "balancingPaymentDate": "2017-10-20",
      "totalLiability": "5007.01"
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
  "reason": "Invalid JSON message received",
  "reasonCode": ""
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
      "startDate": "2017-03-01",
      "endDate": "2017-06-08",
      "firstPaymentDate": "2017-03-09",
      "firstPaymentAmount": "1025.25",
      "regularPaymentAmount": "1025.25",
      "regularPaymentFrequency": "Monthly",
      "reviewDate": "2017-05-30",
      "initials": "ZZZ",
      "enforcementAction": "Distraint",
      "directDebit": true,
      "debitDetails": [
        {
          "debitType": "IN1",
          "dueDate": "2017-01-25"
        }
      ],
      "saNote": "DDI 000800001012, PP 00000000000100004290, First Payment Due Date 09/03/2017, First Payment £1025.25, Regular Payment £1025.25, Frequency Monthly, Final Payment £1038.38, Review Date 29/06/2017"
    },
    "letterAndControl": {
      "customerName": "SSTTPCREDID02",
      "salutation": "Dear  SSTTPCREDID02",
      "addressLine1": "sfsfsdfsdf",
      "addressLine2": "79 St Marys Road",
      "addressLine3": "Sheffield",
      "addressLine4": "South Yorkshire",
      "postCode": "S2 4AH",
      "totalAll": "3075.74",
      "clmIndicateInt": "Including interest due",
      "clmPymtString": "Initial payment of 1025.25 then 1 payments of 1025.25 and final payment of 1038.38",
      "officeName1": "HMRC",
      "officeName2": "DM 440",
      "officePostcode": "BX5 5AB",
      "officePhone": "0300 200 3822",
      "officeFax": "01708 707502",
      "officeOpeningHours": "Monday - Friday 08.00 to 20.00",
      "template": "DMTC13"
    }
}
```

Example JSON responses:

**Error Response**
```
{
  "reason": "Invalid JSON message received",
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
  "reason": "Your submission contains one or more errors",
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
        "creationDate": "2017-11-05"
      },
      "relevantDueDate": "2017-11-05",
      "totalOutstanding": 2500
    },
    {
      "taxYearEnd": "2016-04-05",
      "charge": {
        "originCode": "IN2",
        "creationDate": "2017-11-05"
      },
      "relevantDueDate": "2017-09-25",
      "totalOutstanding": 3250
    }
  ]
}
```

**Error Response**
```
{
  "reason": "Your submission contains one or more errors",
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
  "reason": "Your submission contains one or more errors",
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
    