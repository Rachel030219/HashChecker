package net.rachel030219.hashchecker.tools;
/*
 Copyright 2017 Rachel030219

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

/**
 * Created by rachel on 17-1-19.
 * This class is used to convert hex to dec
 * and convert back.
 */

public class MathTool {
    /**
     * @author Rachel
     * @param dec value that you want to convert
     * @return hexadecimal value
     */
    public static String toHex(long dec){
        return Long.toHexString(dec);
    }
}
