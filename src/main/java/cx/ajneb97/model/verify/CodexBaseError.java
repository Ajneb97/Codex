package cx.ajneb97.model.verify;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class CodexBaseError {

    protected String file;
    protected String errorText;
    protected boolean critical;
    protected String prefix;

    public CodexBaseError(String file, String errorText, boolean critical){
        this.file = file;
        this.errorText = errorText;
        this.critical = critical;
        this.prefix = "&e⚠ ";;
        if(this.critical){
            this.prefix = "&c⚠ ";
        }
    }

    public List<String> getFixedErrorText(){
        List<String> sepText = new ArrayList<String>();
        int currentPos = 0;
        for(int i=0;i<errorText.length();i++) {
            if(currentPos >= 35 && errorText.charAt(i) == ' ') {
                String m = errorText.substring(i-currentPos, i);
                currentPos = 0;
                sepText.add(m);
            }else {
                currentPos++;
            }
            if(i==errorText.length()-1) {
                String m = errorText.substring(i-currentPos+1, errorText.length());
                sepText.add(m);
            }
        }
        return sepText;
    }

    public abstract void sendMessage(Player player);
}
