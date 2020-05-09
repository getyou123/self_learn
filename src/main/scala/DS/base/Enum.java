package DS.base;

public class Enum {
    enum Season{
        SPRING("spring"),//此处调用了构造方法,默认public
        SUMMER("summer");

        private String season;//单例模式

        private Season(String string){this.season=string;};

        public String getSeason() {
            return season;
        }
    }

    public static void main(String[] args) {
        Season season=Season.SPRING;

    }

}

