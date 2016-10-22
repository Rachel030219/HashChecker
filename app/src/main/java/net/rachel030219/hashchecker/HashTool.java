/*
 Copyright 2016 Rachel030219

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
package net.rachel030219.hashchecker;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.DigestInputStream;

public class HashTool{

	public static String getFileHash(String type, File file){

		int bufferSize = 256 * 1024;

		FileInputStream fileInputStream = null;
		
		DigestInputStream digestInputStream = null;
	
		try {
	
			MessageDigest messageDigest = MessageDigest.getInstance(type);
			
			fileInputStream = new FileInputStream(file);
	
			digestInputStream = new DigestInputStream(fileInputStream,messageDigest);
	
			byte[] buffer =new byte[bufferSize];
	
			while (digestInputStream.read(buffer) > 0);
	
			messageDigest= digestInputStream.getMessageDigest();
	
			byte[] resultByteArray = messageDigest.digest();
	
			return byteArrayToHex(resultByteArray);

		} catch (Exception e) {

			return null;

		} finally {
			
			try {

				digestInputStream.close();

			} catch (Exception e) {

			}

			try {

				fileInputStream.close();

			} catch (Exception e) {

			}
		}
		
	}

	public static String byteArrayToHex(byte[] byteArray) {

		char[] hexDigits = {'0','1','2','3','4','5','6','7','8','9', 'A','B','C','D','E','F' };

		char[] resultCharArray =new char[byteArray.length * 2];

		int index = 0;

		for (byte b : byteArray) {

			resultCharArray[index++] = hexDigits[b>>> 4 & 0xf];

			resultCharArray[index++] = hexDigits[b& 0xf];

		}

		return new String(resultCharArray);

	}
	
}
