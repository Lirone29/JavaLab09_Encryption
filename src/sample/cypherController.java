package sample;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ResourceBundle;

public class cypherController implements Initializable {

    private Cipher cipher;
    private Keys keys;
    File initialDirectory = new File("C:\\Users\\Eliza\\Desktop\\PWR\\Sem\\JAVA\\lab09\\package");
    File initialPrivateDirectory = new File("C:\\Users\\Eliza\\Desktop\\PWR\\Sem\\JAVA\\lab09\\package\\keys");
    File initialPublicDirectory = new File("C:\\Users\\Eliza\\Desktop\\PWR\\Sem\\JAVA\\package\\keys");
    String outputDestination = "C:\\Users\\Eliza\\Desktop\\PWR\\Sem\\JAVA\\lab09\\package\\endFiles\\";

    String source;
    FileChooser privateKeyChooser;
    FileChooser publicKeyChooser;
    FileChooser fileChooser;

    @FXML
    private TextField destinationTextField;

    @FXML
    private Label destinationLabel;

    @FXML
    private Button loadPrivateKeyButton;

    @FXML
    private Button encryptButton;

    @FXML
    private Button generateKeysButton;

    @FXML
    private TextField publicKeyTextField;

    @FXML
    private Label fileLabel;

    @FXML
    private TextField privateKeyTextField;

    @FXML
    private Button publicKeyButton;

    @FXML
    private Button selectButton;

    @FXML
    private Button decryptButton;

    public void encryptFile() throws InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        String tmp = outputDestination + destinationTextField.getText();
       // File a = new File(tmp);

        System.out.println(tmp);

        if (new File(source).exists()) {
           this.encrypt(this.getFileInBytes(new File(source)), new File(tmp),this.keys.getPrivateKey());
        } else {
            System.out.println("Nie ma takiego pliku źródłowego");
        }

    }

    public void encrypt(byte[] input, File output, PrivateKey key) throws InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        saveFile(output, this.cipher.doFinal(input));
    }


    public void decryptFile() throws IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        String tmp = outputDestination + destinationTextField.getText();
        if (new File(source).exists()) {
            this.decrypt(this.getFileInBytes(new File(source)),
                    new File(tmp), this.keys.getPublicKey());
        } else {
            System.out.println("Nie ma takiego pliku źródłowego");
        }
    }

    public void decrypt(byte[] input, File output, PublicKey key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        saveFile(output, this.cipher.doFinal(input));
    }

    public void generateKeys(){


        try {
            TextInputDialog td1 = new TextInputDialog("1024");
            td1.setHeaderText("Podaj rozmiar klucza do wygenerowania");
            td1.setContentText("Size");
            td1.setTitle("New Size");
            td1.showAndWait();
            keys = new Keys(Integer.parseInt(td1.getResult()));
            keys.createKeys();
            TextInputDialog td = new TextInputDialog("Key Name");
            td.setHeaderText("Podaj nazwę klucza do wygenerowania");
            td.setContentText(null);
            td.setTitle("New key");
            td.showAndWait();
            keys.writeToFile(initialDirectory.getPath()+"/keys/"+td.getResult()+"-public", keys.getPublicKey().getEncoded());
            keys.writeToFile(initialDirectory.getPath()+"/keys/"+td.getResult()+"-private", keys.getPrivateKey().getEncoded());
        } catch (NoSuchAlgorithmException | IOException e) {
            System.err.println(e.getMessage());
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    public void selectFile(){

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initialDirectory);
        File selectedFile = fileChooser.showOpenDialog(null);
        Path filePath = selectedFile.toPath();
        String name = filePath.getFileName().toString();
        this.source = selectedFile.getAbsolutePath();
        System.out.println("Source == " + source);
        this.fileLabel.setText(name);
    }

    public void saveFile(File output, byte[] toWrite) throws IOException {
       FileOutputStream fos = new FileOutputStream(output);
        fos.write(toWrite);
        fos.flush();
        fos.close();
    }

    public void choosePrivateKey() throws Exception {
        privateKeyChooser = new FileChooser();
        privateKeyChooser.setInitialDirectory(initialPrivateDirectory);
        File selectedFile = privateKeyChooser.showOpenDialog(null);
        Path filePath = selectedFile.toPath();
        String name = filePath.getFileName().toString();
        this.privateKeyTextField.setText(name);
        keys.setPrivate(selectedFile.getAbsolutePath());

    }

    public void choosePublicKey() throws Exception {
        publicKeyChooser = new FileChooser();
        publicKeyChooser.setInitialDirectory(initialPrivateDirectory);
        File selectedFile = publicKeyChooser.showOpenDialog(null);
        Path filePath = selectedFile.toPath();
        String name = filePath.getFileName().toString();
        this.publicKeyTextField.setText(name);
        //nie wiem jaka ściezke podawać

        keys.setPublic(selectedFile.getAbsolutePath());
    }



    private byte[] getFileInBytes(File f) throws IOException {
            FileInputStream fis = new FileInputStream(f);
            byte[] fbytes = new byte[(int) f.length()];
            fis.read(fbytes);
            fis.close();
            return fbytes;
        }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            cipher = Cipher.getInstance("RSA");
            keys = new Keys(1024);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }
    }

