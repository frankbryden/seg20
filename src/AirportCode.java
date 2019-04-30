class AirportCode {
    public final String code;

    public AirportCode(String code){
        this.code = code.toUpperCase();
    }

    public boolean isValid(){
        return this.code.length() == 3;
    }
}
