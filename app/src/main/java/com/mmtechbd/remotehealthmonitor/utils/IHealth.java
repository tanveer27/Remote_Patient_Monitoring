package com.mmtechbd.remotehealthmonitor.utils;

import com.mmtechbd.remotehealthmonitor.BuildConfig;
import com.mmtechbd.remotehealthmonitor.model.AccessToken;

import java.net.MalformedURLException;
import java.net.URL;

import static java.lang.String.format;

public class IHealth
{
	private static final String CLIENT_ID = BuildConfig.CLIENT_ID;
	private static final String CLIENT_SECRET = BuildConfig.CLIENT_SECRET;
	public static final String REDIRECT_URL = BuildConfig.REDIRECT_URL;
	private static final String API_NAME = BuildConfig.API_NAME;
	public static final String AUTH_QUERY_PARAM = "code";
	private static final String SC = BuildConfig.SC;

	public static final String AUTH_URL = format(
		"https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/?client_id=%s&response_type=code&redirect_uri=%s&APIName=%s&RequiredAPIName=%s",
		CLIENT_ID,
		REDIRECT_URL,
		API_NAME,
		API_NAME
	);

	public static String getTokenUrl(String code) {
		// TODO: Implement this method
		return format(
				"https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/?client_id=%s&client_secret=%s&grant_type=authorization_code&redirect_uri=%s&code=%s",
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URL,
				code
		);
	}

	public static String getRefreshTokenUrl(String refreshToken) {
		return format(
				"https://api.ihealthlabs.com:8443/OpenApiV2/OAuthv2/userauthorization/?client_id=%s&client_secret=%s&response_type=refresh_token&redirect_uri=%s&refresh_token=%s",
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URL,
				refreshToken
				);
	}

	private static URL getIHealthDataUrl(AccessToken token, String dataFileName, String sv) throws MalformedURLException {
		String us = format(
				"https://api.ihealthlabs.com/openapiv2/user/%s/%s/?client_id=%s&client_secret=%s&redirect_uri=%s&access_token=%s&sc=%s&sv=%s",
				token.getUserId(),
				dataFileName,
				CLIENT_ID,
				CLIENT_SECRET,
				REDIRECT_URL,
				token.getAccessToken(),
				SC,
				sv
		);
		return new URL(us);
	}

	private static final String SV_SPO2 = BuildConfig.SV_SPO2;
	public static URL getSpo2DataUrl(AccessToken token) throws MalformedURLException {
		return getIHealthDataUrl(token,"spo2.json",SV_SPO2);
	}

	private static final String SV_BP = BuildConfig.SV_BP;
	public static URL getBpDataUrl(AccessToken token) throws MalformedURLException {
		return getIHealthDataUrl(token,"bp.json",SV_BP);
	}

	private static final String SV_BG = BuildConfig.SV_BG;
	public static URL getGlucoseUrl(AccessToken accessToken) throws MalformedURLException {
		return getIHealthDataUrl(accessToken,"glucose.json",SV_BG);
	}

	private static final String SV_WEIGHT = BuildConfig.SV_WEIGHT;
	public static URL getWeightUrl(AccessToken accessToken) throws MalformedURLException {
		return getIHealthDataUrl(accessToken,"weight.json",SV_WEIGHT);
	}

	private static final String SV_ACTIVITY = BuildConfig.SV_ACTIVITY;
	public static URL getActivityUrl(AccessToken accessToken) throws MalformedURLException {
		return getIHealthDataUrl(accessToken,"activity.json",SV_ACTIVITY);
	}
}
