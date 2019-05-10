package com.knoldus.protobuf.akkacluster.serializerapp.exception

object ErrorCodes extends Enumeration
{
    type ErrorCodes = Value

    val SMS_WRONG_VERIFICATION,
    SMS_PARSE_ERROR,
    SMS_VERIFICATION_NO_PHONE_NUMBER,
    LOGIN_WRONG_DETAILS,
    FACEBOOK_ACCESS_FAIL,
    FACEBOOK_LOGIN_FAIL, // 5
    SIGNUP_EXISTING_USER,
    LOGIN_SERVER_ERROR,
    NO_FREE_SIP_CLIENT,
    WRONG_CREDENTIALS,
    CANNOT_UPDATE_DB, // 10
    NON_EXISTENT_CNC_ID,
    NO_CNC_CONNECTED,
    INNER_API_SERVER_ERROR,
    MISSING_CONFIGURATION,
    API_ERROR,//api token authentication error  // 15
    NOT_IMPLEMENTED,
    USER_CANCELED_REQUEST,
    TRYING_UPDATE_INVALID_LINK,
    UNKNOWN_ERROR,
    UNSUPPORTED_API_VERSION, // 20
    NO_REPORTEAM,
    CLIENT_ERROR,
    SMS_VERIFICATION_EXPIRED,
    WATCHME_SESSION_NON_EXISTENT,
    NOT_EXISTING_USER, // 25
    VOICENTER_API_ERROR,
    NO_ORGANIZATION_CODE,
    ERROR_WITH_IGNORE_LOG,
    NO_PHONE_NUMBER_IN_ORGANIZATION,
    API_ABUSE, // 30
    REPORT_EXPIRED,
    UNSUPPORTED_OPERATION,
    INVALID_USER_TYPE_FOR_UPDATE,
    CANNOT_PREDICT_INDOOR_LOCATION,
    CANNOT_BUILD_CLASSIFIER,
    UNAUTHORIZED,
    PARSING_ERROR = Value
}
