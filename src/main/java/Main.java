public class Main {
    // Es static porque pertenece a toda la clase y final porque no se va a modificar
    static final java.sql.Connection con = DatabaseConnection.getConnection();
    public static void main(String[] args) {
        // No hace nada, de momento.
        // En cada método que creemos, usaremos la variable `con`
    }
}


