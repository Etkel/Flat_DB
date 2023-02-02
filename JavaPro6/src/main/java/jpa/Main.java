package jpa;

import javax.persistence.*;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("Flatpers");
            em = emf.createEntityManager();
            try {
                while (true) {
                    System.out.println("1: add flat");
                    System.out.println("2: add random flats");
                    System.out.println("3: delete flat");
                    System.out.println("4: change flat");
                    System.out.println("5: view flats");
                    System.out.println("6: view flats by parameters");
                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1" -> addFlat(sc);
                        case "2" -> insertRandomFlats(sc);
                        case "3" -> deleteFlat(sc);
                        case "4" -> changeFlat(sc);
                        case "5" -> viewFlats();
                        case "6" -> viewFlatsByParameters(sc);
                        default -> {
                            return;
                        }
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void addFlat(Scanner sc) {
        System.out.print("Enter flat district: ");
        String district = sc.nextLine();
        System.out.print("Enter flat address: ");
        String address = sc.nextLine();
        System.out.print("Enter flat square: ");
        String sSquare = sc.nextLine();
        int square = Integer.parseInt(sSquare);
        System.out.print("Enter flat rooms: ");
        String sRooms = sc.nextLine();
        int rooms = Integer.parseInt(sRooms);
        System.out.print("Enter flat price: ");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);


        em.getTransaction().begin();
        try {
            Flat f = new Flat(district, address, square, rooms, price);
            em.persist(f);
            em.getTransaction().commit();


            System.out.println(f.getId());
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void deleteFlat(Scanner sc) {
        System.out.print("Enter flat id: ");
        String fId = sc.nextLine();
        long id = Long.parseLong(fId);

        Flat f = em.getReference(Flat.class, id);
        if (f == null) {
            System.out.println("Flat not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            em.remove(f);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void changeFlat(Scanner sc) {
        System.out.print("Enter flat id: ");
        String fId = sc.nextLine();
        long id = Long.parseLong(fId);

        System.out.print("Enter new flat district: ");
        String district = sc.nextLine();
        System.out.print("Enter new flat address: ");
        String address = sc.nextLine();
        System.out.print("Enter new  flat square: ");
        String sSquare = sc.nextLine();
        System.out.print("Enter new flat rooms: ");
        String sRooms = sc.nextLine();
        System.out.print("Enter new flat price: ");
        String sPrice = sc.nextLine();

        Flat f = em.getReference(Flat.class, id);
        if (f == null) {
            System.out.println("Flat not found!");
            return;
        }

        em.getTransaction().begin();
        try {
            if (!district.isEmpty()) f.setDistrict(district);
            if (!address.isEmpty()) f.setAddress(address);
            if (!sSquare.isEmpty()) f.setSquare(Integer.parseInt(sSquare));
            if (!sRooms.isEmpty()) f.setRooms(Integer.parseInt(sRooms));
            if (!sPrice.isEmpty()) f.setPrice(Double.parseDouble(sPrice));
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void insertRandomFlats(Scanner sc) {
        System.out.print("Enter flats count: ");
        String sCount = sc.nextLine();
        int count = Integer.parseInt(sCount);

        em.getTransaction().begin();
        try {
            for (int i = 0; i < count; i++) {
                Flat c = new Flat(randomDistrict(), "address " + i,
                        RND.nextInt(500), RND.nextInt(10), RND.nextDouble(100000));
                em.persist(c);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void viewFlats() {
        Query query = em.createQuery("SELECT f FROM Flat f", Flat.class);
        List<Flat> list = (List<Flat>) query.getResultList();

        for (Flat f : list)
            System.out.println(f);
    }

    private static void viewFlatsByParameters(Scanner sc) {
        System.out.println("What parameters would you like to choose ?");
        System.out.println("Press 'Enter' to skip");
        System.out.println("Enter District");
        String district = sc.nextLine();
        System.out.println("Enter Address");
        String address = sc.nextLine();
        System.out.print("Would you like to Enter square: ");
        String skip = "Press anything to agree/ Press 'Enter' to skip";
        System.out.println(skip);
        String sSquare = sc.nextLine();
        String squareMin = "";
        String squareMax = "";
        if(!sSquare.isEmpty()) {
            System.out.println("Enter range: from");
            squareMin = sc.nextLine();
            System.out.println("Enter range: to");
            squareMax = sc.nextLine();
        }
        System.out.print("Would you like to Enter rooms: ");
        System.out.println(skip);
        String sRooms = sc.nextLine();
        String roomMin = "";
        String roomMax = "";
        if(!sRooms.isEmpty()) {
            System.out.println("Enter range: from");
            roomMin = sc.nextLine();
            System.out.println("Enter range: to");
            roomMax = sc.nextLine();
        }
        System.out.print("Would you like to Enter price: ");
        System.out.println(skip);
        String sPrice = sc.nextLine();
        String priceMin = "";
        String priceMax = "";
        if(!sPrice.isEmpty()) {
            System.out.println("Enter range: from");
            priceMin = sc.nextLine();
            System.out.println("Enter range: to");
            priceMax = sc.nextLine();
        }
        boolean checker = false;
        StringBuilder sb = new StringBuilder("SELECT f FROM Flat f ");
        if(!district.isEmpty()) {
            sb.append("WHERE ").append("f.district =:d ");
            checker = true;
        }
        if(!address.isEmpty()) {
            if(checker == true) {
                sb.append("AND f.address=:a ");
            } else {
                sb.append("WHERE f.address =:a ");
                checker = true;
            }
        }
        if(!sSquare.isEmpty()) {
            if(checker == true) {
                sb.append("AND f.square BETWEEN ").append(squareMin)
                        .append(" AND ").append(squareMax).append(" ");
            } else {
                sb.append("WHERE f.square BETWEEN ").append(squareMin)
                        .append(" AND ").append(squareMax).append(" ");
                checker = true;
            }
        }
        if(!sRooms.isEmpty()) {
            if(checker == true) {
                sb.append("AND f.rooms BETWEEN ").append(roomMin)
                        .append(" AND ").append(roomMax).append(" ");
            } else {
                sb.append("WHERE f.rooms BETWEEN ").append(roomMin)
                        .append(" AND ").append(roomMax).append(" ");
                checker = true;
            }
        }
        if(!sPrice.isEmpty()) {
            if(checker == true) {
                sb.append("AND f.price BETWEEN ").append(priceMin)
                        .append(" AND ").append(priceMax).append(" ");
            } else {
                sb.append("WHERE f.price BETWEEN ").append(priceMin)
                        .append(" AND ").append(priceMax).append(" ");
                checker = true;
            }
        }
        if (sPrice.isEmpty() && sRooms.isEmpty() && sSquare.isEmpty()
                && address.isEmpty() && district.isEmpty()) {
            viewFlats();
        } else {
            try {
                Query query = em.createQuery(sb.toString(), Flat.class);
                if(!address.isEmpty()) {
                    query.setParameter("a", address);
                }
                if(!district.isEmpty()) {
                    query.setParameter("d", district);
                }
                List<Flat> list = (List<Flat>) query.getResultList();
                for (Flat f : list)
                    System.out.println(f);
            } catch (NoSuchElementException ex) {
                System.out.println("Wrong parameters!");
            }
        }

    }

    static final String[] DISTRICTS = {"downtown", "chinatown", "technic", "seaway", "artificial"};
    static final Random RND = new Random();

    static String randomDistrict() {
        return DISTRICTS[RND.nextInt(DISTRICTS.length)];
    }

}


