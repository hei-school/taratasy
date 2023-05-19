# taratasy

Yet another file API, _taratasy_ being the malagasy word for _file_.

## Motivation

Tired of setting up Amplify Storage authorization through endless configurations of Cognito User Pool, Identity Pool, S3 Policy Document... and so on? Even Firebase Storage failed you because you just had too many additional configurations to perform in Firebase Authentication and Firebase Security Rules?

Then meet _taratasy_, the AWS serverless file API with security made easy.

## Usage

1. Set up authentication, which is as simple as providing the URL of an [iza-compliant](https://github.com/hei-school/iza) authentication API.
2. Set up authorization, which is as simple as providing a [_taratasy_ authorization rules](https://github.com/hei-school/taratasy/blob/main/functions/src/test/resources/authorizations-hei.csv).
3. SAM deploy into your AWS and voilà!

## Cost

_taratasy_ is fully serverless. If you have no consumer, pay nothing. If you have millions of consumers, fear not: _taratasy_ will scale.
