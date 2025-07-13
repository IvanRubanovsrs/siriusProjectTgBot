import java.time.LocalDateTime;

public class Case {
    private String nameCase;
    private String dateTime;
    private Long userId;

    public Case() { }


    public Case(String nameCase, String dateTime, Long userId) {
        this.dateTime = dateTime;
        this.nameCase = nameCase;
        this.userId = userId;
    }

    public String getNameCase() {
        return nameCase;
    }

    public void setNameCase(String nameCase) {
        this.nameCase = nameCase;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Case{" +
                "nameCase='" + nameCase + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", userId=" + userId +
                '}';
    }
}
