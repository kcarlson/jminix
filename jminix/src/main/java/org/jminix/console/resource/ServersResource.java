/* 
 * Copyright 2009 Laurent Bovet, Swiss Post IT <lbovet@jminix.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jminix.console.resource;

import java.util.Arrays;
import java.util.List;

import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;

public class ServersResource extends AbstractListResource
{
    public ServersResource(Context context, Request request, Response response)
    {
        super(context, request, response);
    }

    @Override
    protected List<Object> getList()
    {
        
        Object[] serverIndices = new Object[getServerProvider().getConnections().size()];
        
        for(int i=0; i<serverIndices.length; i++) {
            serverIndices[i]=i;
        }
        
        return Arrays.asList(serverIndices);
    }

}
