/*
 * The MIT License (MIT)
 *
 * Copyright (c) Open Application Platform Authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package oap.ws.sso;

import oap.http.Cookie;
import oap.http.server.nio.HttpServerExchange;
import oap.ws.Response;
import oap.ws.SessionManager;
import org.joda.time.DateTime;

import javax.annotation.Nullable;

import static org.joda.time.DateTimeZone.UTC;

public class SSO {
    public static final String AUTHENTICATION_KEY = "Authorization";
    public static final String SESSION_USER_KEY = "loggedUser";

    @Nullable
    public static String getAuthentication( HttpServerExchange exchange ) {
        String value = exchange.getRequestHeader( AUTHENTICATION_KEY );
        if( value != null ) return value;
        return exchange.getRequestCookieValue( AUTHENTICATION_KEY );
    }

    public static Response authenticatedResponse( Authentication authentication, String cookieDomain, long cookieExpiration, Boolean cookieSecure ) {
        return Response
            .jsonOk()
            .withHeader( AUTHENTICATION_KEY, authentication.id )
            .withCookie( new Cookie( AUTHENTICATION_KEY, authentication.id )
                .withDomain( cookieDomain )
                .withPath( "/" )
                .withExpires( new DateTime( UTC ).plus( cookieExpiration ) )
                .httpOnly( true )
                .secure( cookieSecure )
            )
            .withBody( authentication.view, false );
    }

    public static Response authenticatedResponse( Authentication authentication, String cookieDomain, long cookieExpiration ) {
        return authenticatedResponse( authentication, cookieDomain, cookieExpiration, false );
    }

    public static Response logoutResponse( String cookieDomain ) {
        return Response
            .noContent()
            .withCookie( new Cookie( AUTHENTICATION_KEY, "<logged out>" )
                .withDomain( cookieDomain )
                .withPath( "/" )
                .withExpires( new DateTime( 1970, 1, 1, 1, 1, UTC ) )
            )
            .withCookie( new Cookie( SessionManager.COOKIE_ID, "<logged out>" )
                .withDomain( cookieDomain )
                .withPath( "/" )
                .withExpires( new DateTime( 1970, 1, 1, 1, 1, UTC ) )
            );
    }
}
