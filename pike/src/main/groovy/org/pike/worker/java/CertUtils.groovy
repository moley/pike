package org.pike.worker.java

import groovy.util.logging.Slf4j
import org.apache.commons.codec.binary.Base64

import javax.net.ssl.*
import java.nio.charset.Charset
import java.nio.file.Files
import java.security.KeyStore
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

/**
 * Tool for downloading a certificte from a https server
 * @author oleym
 * Date: 19.08.14
 */
@Slf4j
class CertUtils {

    private static String FS = File.separator

    /**
     * checks if the certificate is already imported in the jdk
     * @param host   host
     * @param jdkHome  home
     * @return  true: is imported, false: is not yet imported
     */
    public static boolean certificateIsImported (final String host, final File jdkHome) {
        final File certsFile = getCertsFile(jdkHome)
        FileInputStream is = new FileInputStream(certsFile)

        KeyStore ks = KeyStore.getInstance(KeyStore.defaultType)

        def changeit = "changeit"
        char[] passphrase = changeit.toCharArray()
        try {
            ks.load(is, passphrase)
        } catch (Exception e) {
            log.error(e.toString(), e)
            return true
        }
        is.close()

        return ks.containsAlias(host)
    }

    public static File getCertsFile (final File jdkHome) {
        return new File (jdkHome, 'jre/lib/security/cacerts')
    }

    /**
     * Downloads the ssl certificate from the given host, verifies, if it is already trusted, if it is, returns null, else
     * it returns the command line call how to import the certificate with the keytool from jre. This can not be done
     * programmatically, because importing a certificate in the jre CA store requires administration rights
     *
     * @param host String hostName without prefixed "https://"
     * @return String
     */
    public static String readCertificateAndWriteToFile(String host, final File jdkHome)  {
        if (certificateIsImported(host, jdkHome))
            return null

        final File certsFile = getCertsFile(jdkHome)

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.defaultAlgorithm)
        KeyStore ks = KeyStore.getInstance(KeyStore.defaultType)
        tmf.init(ks)
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.trustManagers[0]
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager)
        context.init(null, [tm] as TrustManager[], null)
        SSLSocketFactory factory = context.socketFactory

        SSLSocket socket = (SSLSocket) factory.createSocket(host, 443)
        socket.soTimeout = 10000
        try {
            socket.startHandshake()
            socket.close()
            return null
        } catch (SSLException e) {

        }

        X509Certificate[] chain = tm.chain
        if (chain == null) {
            throw new IllegalStateException("Certificate of https://$host could not be otbtained, has to be imported manually")
        }
        int k = 0

        X509Certificate cert = chain[k]

        //Download certificate to temp
        File tmpFile = Files.createTempFile('cert' + System.currentTimeMillis(), 'pike').toFile()
        FileOutputStream os = new FileOutputStream(tmpFile)
        Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"))
        wr.write(convertToPem(cert))
        wr.flush()
        os.close()

        File bin = new File (jdkHome, 'bin')
        File keytool = new File (bin, 'keytool')

        if (! keytool.canExecute())
            if (keytool.setExecutable(true) == false)
                throw new IllegalStateException("Could not make ${keytool} executable")

        return "$keytool -import -alias $host -file $tmpFile.absolutePath -keystore $certsFile.absolutePath -storepass changeit -noprompt"
    }


    protected static String convertToPem(X509Certificate cert) throws CertificateEncodingException {
        Base64 encoder = new Base64(64);
        String cert_begin = "-----BEGIN CERTIFICATE-----\n";
        String end_cert = "-----END CERTIFICATE-----";

        byte[] derCert = cert.encoded
        String pemCertPre = new String(encoder.encode(derCert))
        String pemCert = cert_begin + pemCertPre + end_cert
        return pemCert
    }


    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm
        private X509Certificate[] chain

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException()
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException()
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain
            tm.checkServerTrusted(chain, authType)
        }
    }

}
