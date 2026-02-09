import java.sql.*;

public class DatabaseConnection
{
    private static Connection con;
    public static Connection getConnection(){
        try {
            // Poned la ruta correcta, de lo contrario, os creará un a bd nueva
            String host = "jdbc:sqlite:src/main/resources/empresa.bd";
            con = DriverManager.getConnection(host);
            System.out.println("Conexión realizada");
        } catch (java.sql.SQLException ex) {
            System.out.println("Se ha producido un error al conectar: " + ex.getMessage());
        }
        return con;
    }
    public static void insertarEmpleado(int numero, String nombre, int departamento, int edad, float sueldo) throws SQLException {
        // Creamos el SQL. En cada dato ponemos un ?
        String sql = "INSERT INTO empleados (num, nombre, departamento, edad, sueldo)";
        sql += " VAlUES(?, ?, ?, ?, ?)";

        // Preparamos el SQL
        PreparedStatement st = DatabaseConnection.con.prepareStatement(sql);

        // Y ahora hemos de rellenar los datos, teniendo cuidado de que los tipos
        // de la tabla coincidan con el tipo de setter;
        st.setInt(1, numero);
        st.setString(2, nombre);
        st.setInt(3, departamento);
        st.setInt(4, edad);
        st.setFloat(5, sueldo);

        // Y no nos olvidemos de hacer executeUpdate();
        st.executeUpdate();

        // Imprimimos para ver los resultados
        imprimirEmpleados();
}
    public static void imprimirEmpleados() throws SQLException {
        // Como no va a tener parámetros, no hace falta crear preparedStatement
        Statement st = con.createStatement();

        // Creamos el SQL que al ser de consulta se hace con executeQuery()
        ResultSet rs = st.executeQuery("SELECT * FROM empleados");

        System.out.println("Núm.\tNombre\tDepartamento\tEdad\t Sueldo");
        // Y ahora recorremos los datos
        while (rs.next()){
            // Hemos de hacer coincidir el tipo de getter con
            // el tipo de dato de la tabla
            System.out.print(rs.getInt(1) + "\t");
            System.out.print(rs.getString(2) + "\t");
            System.out.print(rs.getInt(3) + "\t");
            System.out.print(rs.getInt(4) + "\t");
            System.out.println(rs.getFloat(5) + "\t");
        }
    }
    private static void actualizarEmpleados() throws SQLException {
        // Como no va a tener parámetros, no hace falta crear preparedStatement
        // Cuando los datos vengan de fuera de la aplicación, por ejemplo, un
        // usuario de una web que envía datos, es OBLIGADO crear un preparedStatement
        // para evitar ataques de inyección de SQL
        Statement st = con.createStatement();

        // Creamos el SQL que al ser de modificación de datos se hace con executeUpdate()
        String sql = "UPDATE empleados set sueldo = sueldo * 1.05";
        st.executeUpdate(sql);

        sql = "UPDATE empleados set departamento = 20 WHERE num = 3";
        st.executeUpdate(sql);
    }


}


