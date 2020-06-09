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

package oap.ws.admin;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import oap.application.Kernel;
import oap.http.HttpResponse;
import oap.jpath.JPath;
import oap.jpath.JPathOutput;
import oap.jpath.Pointer;
import oap.json.Binder;
import oap.util.Try;
import oap.ws.WsMethod;
import oap.ws.WsParam;
import org.apache.http.entity.ContentType;

import java.io.OutputStreamWriter;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.nio.charset.StandardCharsets.UTF_8;
import static oap.http.Request.HttpMethod.GET;
import static oap.ws.WsParam.From.QUERY;

/**
 * Created by igor.petrenko on 2020-06-06.
 */
@Slf4j
public class JPathWS {
    private final Kernel kernel;

    public JPathWS( Kernel kernel ) {
        this.kernel = kernel;
    }

    @WsMethod( method = GET, path = "/" )
    public HttpResponse get( @WsParam( from = QUERY ) String query ) {
        log.debug( "query = {}", query );
        try {


            return HttpResponse.outputStream( Try.consume( out -> {
                try( var osw = new OutputStreamWriter( out, UTF_8 ) ) {
                    JPath.evaluate( query, kernel.services, new JPathOutput() {
                        @Override
                        public void write( Pointer pointer ) {
                            Binder.json.marshal( pointer.get(), osw );
                        }
                    } );
                }
            } ), ContentType.APPLICATION_JSON ).response();
        } catch( Exception e ) {
            log.error( e.getMessage(), e );

            return HttpResponse.status( HTTP_BAD_REQUEST ).withReason( e.getMessage() )
                .withContent( Throwables.getStackTraceAsString( e ), ContentType.DEFAULT_TEXT )
                .response();
        }
    }
}
