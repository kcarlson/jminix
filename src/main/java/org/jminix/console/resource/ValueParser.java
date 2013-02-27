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

public class ValueParser
{
    public Object parse(String value, String type)
    {
        Object result=null;
        
        if( type.equals("java.lang.String") ) {
            return value;
        } else if( type.equals("java.lang.Byte") || type.equals("byte")) {
            result = Byte.parseByte(value);
        } else if( type.equals("java.lang.Short") || type.equals("short")) {
            result = Short.parseShort(value);
        } else if( type.equals("java.lang.Integer") || type.equals("int")) {
            result = Integer.parseInt(value);
        } else if( type.equals("java.lang.Long") || type.equals("long")) {
            result = Long.parseLong(value);
        } else if( type.equals("java.lang.Boolean") || type.equals("boolean")) {
            result = Boolean.parseBoolean(value);
        } else if( type.equals("java.lang.Float") || type.equals("float")) {
            result = Float.parseFloat(value);
        } else if( type.equals("java.lang.Double") || type.equals("double")) {
            result = Double.parseDouble(value);
        } else if ( type.equals("[Ljava.lang.String;")) {
            result = value.split(",");
        }
        
        if(result==null) {
            throw new RuntimeException("Type "+type+" is not supported");
        }
        
        return result;
    }
}
