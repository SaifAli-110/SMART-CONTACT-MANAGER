package in.sp.main.Helper;

//This class is represented some important message
public class Message {

    private String Content;
    private String Type;

    public Message(String content, String type) {
        Content = content;
        Type = type;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
