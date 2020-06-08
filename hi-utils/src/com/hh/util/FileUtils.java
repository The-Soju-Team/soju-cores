package com.hh.util;

import com.google.common.io.Files;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * @since 26/03/2014
 * @author HienDM1
 */
public class FileUtils {
    /**
     * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the
     * Unicode character set
     */
    public static final String US_ASCII = "US-ASCII";
    /**
     * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * Eight-bit UCS Transformation Format
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * Sixteen-bit UCS Transformation Format, big-endian byte order
     */
    public static final String UTF_16BE = "UTF-16BE";
    /**
     * Sixteen-bit UCS Transformation Format, little-endian byte order
     */
    public static final String UTF_16LE = "UTF-16LE"; // su dung cho tieng viet
    /**
     * Sixteen-bit UCS Transformation Format, byte order identified by an
     * optional byte-order mark
     */
    public static final String UTF_16 = "UTF-16";
    
    /**
     * Hàm ghi chuỗi dữ liệu ra File
     * ví dụ: writeStringToFile(String.format("Hello%sthere!",System.getProperty("line.separator")),"D:\\test.txt", UTF_16LE)
     * 
     * @since 26/03/2014 HienDM
     * @param content nội dung chuỗi dữ liệu, để xuống dòng sử dụng
     * "line.separator" ví dụ:
     * String.format("Hello%sthere!",System.getProperty("line.separator"));
     * @param outputFilePath đường dẫn file
     * @param charset Bộ mã ký tự
     */
    public void writeStringToFile(String content, String outputFilePath, String charset) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(outputFilePath);
        OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
        BufferedWriter outputFile = new BufferedWriter(osw);        
        try {
            outputFile.write(content);
        } finally {
            outputFile.close();
            osw.close();
            fos.close();
        }
    }
    
    /**
     * Hàm ghi chuỗi dữ liệu ra File
     * ví dụ: writeStringToFile(String.format("Hello%sthere!",System.getProperty("line.separator")),"D:\\test.txt", UTF_16LE)
     * 
     * @since 26/03/2014 HienDM
     * @param content nội dung chuỗi dữ liệu, để xuống dòng sử dụng
     * "line.separator" ví dụ:
     * String.format("Hello%sthere!",System.getProperty("line.separator"));
     * @param outputFilePath đường dẫn file
     * @param charset Bộ mã ký tự
     */
    public void writeContinueStringToFile(String content, String outputFilePath, String charset) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        FileOutputStream fos = new FileOutputStream(outputFilePath, true);
        OutputStreamWriter osw = new OutputStreamWriter(fos, charset);
        BufferedWriter outputFile = new BufferedWriter(osw);        
        try {
            outputFile.write(content);
        } finally {
            outputFile.close();
            osw.close();
            fos.close();
        }
    }    
    
    /**
     * Hàm ghi dữ liệu trong File ra chuỗi
     * ví dụ: String content = writeFileToString("D:\\test.txt",UTF_16LE)
     * 
     * @since 26/03/2014 HienDM
     * @param inputFilePath đường dẫn file
     * @return chuỗi dữ liệu
     * @param charset Bộ mã ký tự
     */
    public String readFileToString(String inputFilePath, String charset) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        File inputFile = new File(inputFilePath);
        return readFileToString(inputFile, charset);
    }
    
    /**
     * Hàm ghi dữ liệu trong File ra chuỗi
     * ví dụ: String content = writeFileToString("D:\\test.txt",UTF_16LE)
     * 
     * @since 26/03/2014 HienDM
     * @param inputFile file
     * @return chuỗi dữ liệu
     * @param charset Bộ mã ký tự
     */
    public String readFileToString(File inputFile, String charset) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        StringBuilder returnValue = new StringBuilder();
        try (
                FileInputStream inputStream = new FileInputStream(inputFile);
                InputStreamReader isr = new InputStreamReader(inputStream, charset);
                BufferedReader br = new BufferedReader(isr);){
            StringBuilder line =  new StringBuilder();
            while (!(line.append(br.readLine())).toString().equals("null")) {
                returnValue.append(line);
                returnValue.append(System.getProperty("line.separator"));
                line =  new StringBuilder();
            }
        }
        return returnValue.toString();
    }
    
    public void writeStringToFileNIO(File file, String content, String charset) throws IOException {
        final byte[] messageBytes = content.getBytes(Charset.forName(charset));
        final RandomAccessFile raf = new RandomAccessFile(file, "rw");
        raf.seek(raf.length());
        final FileChannel fc = raf.getChannel();
        final MappedByteBuffer mbf = fc.map(FileChannel.MapMode.READ_WRITE, fc.
                position(), messageBytes.length);
        fc.close();
        mbf.put(messageBytes);
    }    
        
    /**
     * Kiểm tra an toàn thông tin đường dẫn file
     *
     * @param filePath Tên file
     * @return Đường dẫn file có đảm bảo an toàn thông tin không
     */
    public static boolean checkSafeFileName(String filePath) {
        for (int i = 0; i < filePath.length(); i++) {
            char c = filePath.charAt(i);
            if (c == 0) {
                return false;
            } else if (c == '.') {
                char c2 = filePath.charAt(i + 1);
                if (c2 == '.') {
                    char c3 = filePath.charAt(i + 2);
                    if (c3 == '\\' || c3 == '/') {
                        return false;
                    }
                } else if (c2 == '\\' || c2 == '/') {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Kiểm tra file có đúng định dạng không
     *
     * @param fileName Tên file
     * @param allowedList Các định dạng file được phép
     * @return File có đúng định dạng hay không
     */
    public static boolean isAllowedType(String fileName, String allowedList) {
        if (fileName != null && !fileName.trim().equals("")) {
            String[] allowedType = allowedList.split(",");
            String ext = extractFileExt(fileName).toLowerCase().substring(1);
            for (int i = 0; i < allowedType.length; i++) {
                if (allowedType[i].equals(ext)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    /**
     * Lấy đuôi file (định dạng file)
     *
     * @param fileName Tên file
     * @return Đuôi file (định dạng file)
     */    
    public static String extractFileExt(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        String extension = fileName.substring(dotPos);
        return extension;
    }

    /**
     * Lấy tên file sau khi cắt đuôi file
     *
     * @param fileName Tên file
     * @return Tên file sau khi cắt đuôi file
     */
    public static String extractFileNameNotExt(String fileName) {
        int dotPos = fileName.lastIndexOf(".");
        String fileNameNotExt = dotPos > 0 ? fileName.substring(0, dotPos) : fileName;

        return fileNameNotExt;
    }    
    
    /**
     * Ghi dữ liệu từ inputStream tới file
     *
     * @param is input stream
     * @param filePath Tên file
     */
    public static void writeFileFromInputStream(InputStream is, String filePath) throws FileNotFoundException, IOException {
        OutputStream os = new FileOutputStream(filePath);
        try {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);       
        } finally {
            is.close();
            os.close();
        }
    }
    
    /**
     * Hàm copy File
     *
     * @param srcPath File nguồn
     * @param destPath File đích
     * @param isReplace thay thế hay không
     * @return 1:Thành công, -1:Thất bại vì file nguồn không tồn tại, 0:Thất bại vì file đích đã tồn tại
     */
    public static int copyFile(String srcPath, String destPath, boolean isReplace) throws IOException {
        File srcFile = new File(srcPath);
        if(srcFile.exists()) {
            File destFile = new File(destPath);
            if(destFile.exists()) {
                if(isReplace) destFile.delete();
                else return 0;
            }
            Files.copy(srcFile, destFile);
        } else return -1;
        return 1;
    }  
    
    public String readLastLine(File file) throws FileNotFoundException, IOException {
        RandomAccessFile fileHandler = null;
        try {
            fileHandler = new RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    }
                    break;

                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    }
                    break;
                }

                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                    /* ignore */
                }
            }
        }
    }
    
    public String readLastLines(File file, int lines) throws FileNotFoundException, IOException {
        java.io.RandomAccessFile fileHandler = null;
        try {
            fileHandler
                    = new java.io.RandomAccessFile(file, "r");
            long fileLength = fileHandler.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer < fileLength) {
                        line = line + 1;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1) {
                        line = line + 1;
                    }
                }
                if (line >= lines) {
                    break;
                }
                sb.append((char) readByte);
            }

            String lastLine = sb.reverse().toString();
            return lastLine;
        } finally {
            if (fileHandler != null) {
                try {
                    fileHandler.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static byte[] objectToByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos);) {
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        }
        return bytes;
    }

    public static Object byteArrayToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        Object obj = null;
        try (
                ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                ObjectInputStream ois = new ObjectInputStream(bis);) {
            obj = ois.readObject();
        }
        return obj;
    }    
    
    public static byte[] hexToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }
    
    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public void zip(String srcFile, String destFile) throws Exception {
        FileOutputStream fos = new FileOutputStream(destFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(srcFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        final byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        zipOut.close();
        fis.close();
        fos.close();        
    }
    
    public void zipLstFile(List<String> srcFiles, String destFile) throws Exception {
        FileOutputStream fos = new FileOutputStream(destFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        for (String srcFile : srcFiles) {
            File fileToZip = new File(srcFile);
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOut.putNextEntry(zipEntry);
 
            byte[] bytes = new byte[1024];
            int length;
            while((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        zipOut.close();
        fos.close();        
    }
    
    public void zipFolder(String srcFolder, String destFile) throws Exception {
        FileOutputStream fos = new FileOutputStream(destFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(srcFolder);
 
        zipEachFileInFolder(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();        
    }
    
    private void zipEachFileInFolder(File fileToZip, String fileName, ZipOutputStream zipOut) throws Exception {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipEachFileInFolder(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();        
    }
    
    public void unzip(String fileZip, String folder) throws Exception {
        File newFolder = new File(folder);
        if(!newFolder.exists()) newFolder.mkdir();
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while(zipEntry != null){
            String fileName = zipEntry.getName();
            File newFile = new File(folder + File.separator + fileName);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();        
    }    
}
