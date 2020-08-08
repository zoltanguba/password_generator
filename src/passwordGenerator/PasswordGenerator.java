package passwordGenerator;

public class PasswordGenerator {

    private int passwordLength;

    public PasswordGenerator(int passwordLength){
        this.passwordLength = passwordLength;
    }

    public String generateRandomPassword(){

        String characterList = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz#&@?+-!%=()";
        char[] newlyGeneratedPassword = new char[this.passwordLength];

        for (int i = 0; i < this.passwordLength; i++){
            int rand = (int) (Math.random() * characterList.length());
            newlyGeneratedPassword[i] = characterList.charAt(rand);
        }

        return new String(newlyGeneratedPassword);
    }

    public void setPasswordLength(int newPasswordLength){
        this.passwordLength = newPasswordLength;
    }

    public int getPasswordLength(){return this.passwordLength;}


    }


