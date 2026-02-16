package network;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Network{
    private static Connection con;
    // El menú tiene dos pantallas. Se empieza por la primera
    private static Integer currentScreen = 0;
    // Estas dos variables de clase son para guardar los datos del usuario logeado
    private static Integer userId;
    private static String userName;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        String host = "jdbc:sqlite:src/main/resources/network.bd";
        con = java.sql.DriverManager.getConnection(host);
        printBanner();
        int option;
        // Almacena el id del post a comentar o a dar like
        int postTo;
        boolean exists;
        while (true) {
            printMenu();
            try {
                option = Integer.parseInt(sc.next());
            } catch (NumberFormatException e) {
                option = -1;
            }
            // Borrar el \n final
            sc.nextLine();
            // exit acaba la ejecución del programa
            if (option == 0) System.exit(0);
            switch (option) {
                case 1:
                    currentScreen = 0;
                    allPosts();
                    pulseParaContinuar();
                    break;
                case 2:
                    currentScreen = 0;
                    if (!login()){
                        System.out.print(AnsiColor.RED.getCode());
                        System.out.println("User not found");
                        System.out.print(AnsiColor.RESET.getCode());
                        pulseParaContinuar();
                    }else{
                        currentScreen = 1;
                    }
                    break;
                case 3:
                    currentScreen = 0;
                    newUser();
                    break;
                case 4:
                    if (currentScreen == 0)
                        continue;
                    myPosts();
                    pulseParaContinuar();
                    currentScreen = 1;
                    break;
                case 5:
                    if (currentScreen == 0)
                        continue;
                    post();
                    currentScreen = 1;
                    break;
                case 6:
                    if (currentScreen == 0)
                        continue;
                    otherPosts();
                    postTo = selectPostTo();
                    exists = checkPostExists(postTo);
                    if (exists){
                        insertComment(postTo);
                    }else{
                        System.out.print(AnsiColor.RED.getCode());
                        System.out.println("Post not found ");
                        System.out.print(AnsiColor.RESET.getCode());
                    }
                    currentScreen = 1;
                    break;
                case 7:
                    if (currentScreen == 0)
                        continue;

                    otherPosts();
                    postTo = selectPostTo();
                    exists = checkPostExists(postTo);
                    if (exists){
                        like(postTo);
                    }else{
                        System.out.print(AnsiColor.RED.getCode());
                        System.out.println("Post not found ");
                        System.out.print(AnsiColor.RESET.getCode());
                    }
                    currentScreen = 1;
                    break;
                case 8:
                    if (currentScreen == 0)
                        continue;
                    otherPosts();
                    break;
                case 9:
                    currentScreen = 0;
                    userId = -1;
                    userName = "";
                    break;
            }
        }

    }

    private static void pulseParaContinuar() {
        System.out.print(AnsiColor.BLUE.getCode());
        System.out.println("Presiona enter para continuar...");
        System.out.print(AnsiColor.RESET.getCode());
        sc.nextLine();
    }

    /**
     *
     * @param postToCheck - Id del post a comprobar
     * @return True si el post existe
     * @throws SQLException
     */
    private static boolean checkPostExists(int postToCheck) throws SQLException {
        PreparedStatement st = null;
        String query = "SELECT * FROM posts WHERE id = ?";
        st = con.prepareStatement(query);
        st.setInt(1, postToCheck);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Devuelve todos los posts del usuario logeado con id = userId
     * @throws SQLException
     */
    private static void myPosts() throws SQLException {
        PreparedStatement st = null;
        String query = "SELECT posts.id, posts.text, users.name FROM posts JOIN users ON posts.userId = users.id WHERE userId = ?";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("name") + ": " + rs.getString("text"));
            printComments(rs.getInt("id"));
        }
    }

    /**
     * Debe mostrar todos los posts, y después debe pedir al usuario un id de post
     * @return
     */
    private static int selectPostTo() {
        System.out.println("Selecciona un post: ");
        PreparedStatement st = null;
        String query = "SELECT posts.id, posts.text, users.name FROM posts JOIN users ON posts.userId = users.id";
        try {
            st = con.prepareStatement(query);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name") + ": " + rs.getString("text"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            int id = Integer.parseInt(sc.next());
            sc.nextLine();
            return id;
        } catch (NumberFormatException e) {
            sc.nextLine();
            return 0;
        }
    }

    /**
     * Insterta un comentario para el post con id igual a postToComment
     * @param postToComment
     * @throws SQLException
     */
    private static void insertComment(int postToComment) throws SQLException{
        PreparedStatement st = null;
        String query = "INSERT INTO comments (userId, postId, text) VALUES (?, ?, ?)";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        st.setInt(2, postToComment);
        System.out.print("Introduce tu comentario: ");
        String comment = sc.nextLine();
        st.setString(3, comment);
        st.executeUpdate();
    }

    /**
     * Da un like al post con id igual a postToLike
     * @param postToLike
     * @throws SQLException
     */
    private static void like(int postToLike) throws SQLException{
        PreparedStatement st = null;
        String query = "INSERT INTO likes (userId, postId) VALUES (?, ?)";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        st.setInt(2, postToLike);
        st.executeUpdate();
    }

    /**
     * Devuelve los posts de los usuarios que no sean el usuario logeado
     * @throws SQLException
     */
    private static void otherPosts() throws SQLException {
        PreparedStatement st = null;
        String query = "SELECT posts.id, posts.text, users.name FROM posts JOIN users ON posts.userId = users.id WHERE userId != ?";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("name") + ": " + rs.getString("text"));
            printComments(rs.getInt("id"));
        }
    }

    /**
     * Devuelve todos los posts
     * @throws SQLException
     */
    private static void allPosts() throws SQLException {
        PreparedStatement st = null;
        String query = "SELECT posts.id, posts.text, users.name FROM posts JOIN users ON posts.userId = users.id";
        st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println(rs.getString("name") + ": " + rs.getString("text"));
            printComments(rs.getInt("id"));
        }
    }

    /**
     * Imprime todos los comentarios del post con id postId
     * @param postId
     * @throws SQLException
     */
    private static void printComments(int postId) throws SQLException {
        PreparedStatement st = null;
        String query = "SELECT comments.text, users.name FROM comments JOIN users ON comments.userId = users.id WHERE postId = ?";
        st = con.prepareStatement(query);
        st.setInt(1, postId);
        ResultSet rs = st.executeQuery();
        while (rs.next()) {
            System.out.println("\t" + rs.getString("name") + " commented: " + rs.getString("text"));
        }
    }

    /**
     * Comprueba si el usuario existe en la base de datos.
     * En tal caso, inicia las variables userId y userName
     * @return
     * @throws SQLException
     */
    private static boolean login() throws SQLException {
        System.out.print(AnsiColor.BLUE.getCode());
        System.out.print("Name: ");
        String name = sc.nextLine();
        PreparedStatement st = null;
        String query = "SELECT * FROM users WHERE name = ?";
        st = con.prepareStatement(query);
        st.setString(1, name);
        ResultSet rs = st.executeQuery();
        if (rs.next()) {
            userId = rs.getInt("id");
            userName = rs.getString("name");
            currentScreen = 1;
            return  true;
        } else {
            return false;
        }

    }

    /**
     * Inserta un nuevo usuario en la base de datos
     * @throws SQLException
     */
    private static void newUser() throws SQLException{
        System.out.print("Introduce tu nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Introduce tu apellido: ");
        String apellido = sc.nextLine();
        PreparedStatement st = null;
        String query = "INSERT INTO users (name, lastName) VALUES (?, ?)";
        st = con.prepareStatement(query);
        st.setString(1, nombre);
        st.setString(2, apellido);
        st.executeUpdate();
    }

    /**
     * Inserta un nuevo post en la base de datos con el usuario logeado
     * @throws SQLException
     */
    private static void post() throws SQLException {
        System.out.print("Introduce tu post: ");
        String post = sc.nextLine();
        PreparedStatement st = null;
        String query = "INSERT INTO posts (userId, text, likes) VALUES (?, ?, 0)";
        st = con.prepareStatement(query);
        st.setInt(1, userId);
        st.setString(2, post);
        st.executeUpdate();
    }

    public static void printMenu() {
        System.out.println(AnsiColor.BLUE.getCode());
        System.out.println("-----------------------------------------------------------------------------------------------");
        if (currentScreen == 0)
            System.out.println("0 Exit | 1 All Posts | 2 Login | 3 Register");
        else if (currentScreen == 1) {
            System.out.println("0 Exit | 4 My Posts | 5 New Post | 6 New Comment | 7 Like | 8 Other's Posts | 9 Logout " + userName);
        }
        System.out.println("-----------------------------------------------------------------------------------------------");
        System.out.println(AnsiColor.RESET.getCode());
    }

    private static void printBanner() {
        System.out.println(AnsiColor.BLUE.getCode());
        System.out.println("   _____            _       _   _   _      _                      _    ");
        System.out.println("  / ____|          (_)     | | | \\ | |    | |                    | |   ");
        System.out.println(" | (___   ___   ___ _  __ _| | |  \\| | ___| |___      _____  _ __| | __");
        System.out.println("  \\___ \\ / _ \\ / __| |/ _` | | | . ` |/ _ \\ __\\ \\ /\\ / / _ \\| '__| |/ /");
        System.out.println("  ____) | (_) | (__| | (_| | | | |\\  |  __/ |_ \\ V  V / (_) | |  |   < ");
        System.out.println(" |_____/ \\___/ \\___|_|\\__,_|_| |_| \\_|\\___|\\__| \\_/\\_/ \\___/|_|  |_|\\_\\");
        System.out.println(AnsiColor.RESET.getCode());
    }
}
