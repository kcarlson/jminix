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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import net.sf.json.JSONSerializer;

import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

public class OperationResource extends AbstractTemplateResource
{

    public OperationResource(Context context, Request request, Response response)
    {
        super(context, request, response);
    }

    private String templateName = "operation";

    @Override
    protected Map<String, Object> getModel()
    {
        String domain = getRequest().getAttributes().get("domain").toString();

        String mbean = new EncoderBean().decode(getRequest().getAttributes().get("mbean").toString());

        String declaration = new EncoderBean().decode(getRequest().getAttributes().get("operation").toString());

        String operation = declaration.split("\\(")[0];

        String signature = declaration.split("\\(").length > 1 ?
                               (declaration.split("\\(")[1].split("\\)").length >0 ?
                                 declaration.split("\\(")[1].split("\\)")[0] : "") : "";

        Map<String, Object> model = new HashMap<String, Object>();

        model.put("operation", getOperation(getServer(), domain, mbean, operation, signature));

        return model;
    }

    @Override
    public void acceptRepresentation(Representation entity) throws ResourceException
    {
        String[] stringParams = new Form(entity).getValuesArray("param");


        String domain = getRequest().getAttributes().get("domain").toString();

        String mbean = new EncoderBean().decode(getRequest().getAttributes().get("mbean").toString());

        String declaration = new EncoderBean().decode(getRequest().getAttributes().get("operation").toString());

        String operation = declaration.split("\\(")[0];

        String[] signature = declaration.split("\\(").length > 1 ? (
                               declaration.split("\\(")[1].split("\\)").length >0 ?
                                    declaration.split("\\(")[1].split("\\)")[0].split(",") :
                                        new String[]{} ) : new String[]{};

        MBeanServerConnection server = getServer();

        try
        {

            Object[] params=new Object[signature.length];

            ValueParser parser = new ValueParser();
            for(int i=0; i<stringParams.length; i++) {
                params[i] = parser.parse(stringParams[i], signature[i]);
            }

            Object result = server.invoke(new ObjectName(domain+":"+mbean), operation, params, signature);

            if(result != null) {
            	Variant variant = getPreferredVariant();
            	if (MediaType.APPLICATION_JSON == variant.getMediaType()) {
                    getResponse().setEntity(
                            JSONSerializer.toJSON(result).toString(),
                            MediaType.APPLICATION_JSON);
            	} else {
            		getResponse().setEntity(result.toString(), MediaType.TEXT_PLAIN);
            	}
            } else {
            	String queryString = getQueryString();
            	if(!queryString.contains("ok=1")) {
                	if(queryString==null || "".equals(queryString)) {
                		queryString = "?";
                	} else {
                		queryString += "&";
                	}
                	queryString+="ok=1";
            	}
                getResponse().redirectPermanent(new EncoderBean().encode(declaration)+queryString);
            }
        }
        catch (InstanceNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (MalformedObjectNameException e)
        {
            throw new RuntimeException(e);
        }
        catch (MBeanException e)
        {
            throw new RuntimeException(e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean allowPost()
    {
        return true;
    }

    @Override
    protected String getTemplateName()
    {
        return templateName;
    }

    private MBeanOperationInfo getOperation(MBeanServerConnection server, String domain, String mbean,
                                            String operationName, String signature)
    {
        try
        {

            for (MBeanOperationInfo i : server.getMBeanInfo(new ObjectName(domain+":"+mbean)).getOperations())
            {

                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (MBeanParameterInfo p : i.getSignature())
                {
                    if (!first)
                    {
                        sb.append(",");
                    } else {
                        first = false;
                    }
                    sb.append(p.getType());
                }

                if (i.getName().equals(operationName))
                {
                    if (sb.toString().equals(signature))
                    {
                        return i;
                    }
                }
            }
            return null;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstanceNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        catch (MalformedObjectNameException e)
        {
            throw new RuntimeException(e);
        }
        catch (ReflectionException e)
        {
            throw new RuntimeException(e);
        }
        catch (IntrospectionException e)
        {
            throw new RuntimeException(e);
        }
    }

}
