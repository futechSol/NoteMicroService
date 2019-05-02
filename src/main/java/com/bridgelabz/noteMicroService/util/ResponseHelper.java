package com.bridgelabz.noteMicroService.util;

import com.bridgelabz.noteMicroService.model.Response;

public class ResponseHelper {
   public static Response getResponse(int statusCode, String statusMessage, String userToken) {
	   return new Response(statusCode,statusMessage,userToken);
   }
}
