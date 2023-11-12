import com.hepl.protocol.VESPAP;

public class TestAlgoCardNumCheckApp {

    public static void main(String[] args) {
        String[] cardTest = new String[4];
        cardTest[0] = "4990110201830859";//visa card
        cardTest[1] = "4990110201830858";// not a visa card
        cardTest[2] = "123456";//less than 16
        cardTest[3] = "123456789012345m";// 16 char but not only numbers

        int i = 1;
        for (String number :
                cardTest) {
            System.out.println("Card " + i + " :");
            i++;
            if (number.matches("[0-9]+") && number.length() == 16)
                System.out.println(VESPAP.checkVisaCard(number));
            else
                System.out.println("Wrong format (number char<16 or contains something else than numbers)");
        }

    }
}
