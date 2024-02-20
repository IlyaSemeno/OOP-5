package notebook.model.repository.impl;

import notebook.util.UserValidator;
import notebook.util.mapper.impl.UserMapper;
import notebook.model.User;
import notebook.model.repository.GBRepository;

import java.io.*;
import java.util.*;

public class UserRepository implements GBRepository {
    private final UserMapper mapper;
    private final String fileName;

    public UserRepository(String fileName) {
        this.mapper = new UserMapper();
        this.fileName = fileName;
        createDB(); // Метод создания файла можно также переместить сюда
    }

    @Override
    public List<User> findAll() {
        List<String> lines = readAll();
        List<User> users = new ArrayList<>();
        for (String line : lines) {
            users.add(mapper.toOutput(line));
        }
        return users;
    }

    @Override
    public User create(User user) {
        UserValidator uv = new UserValidator();
        user= uv.validate(user);
        List<User> users = findAll();
        long max = 0L;
        for (User u : users) {
            long id = u.getId();
            if (max < id){
                max = id;
            }
        }
        long next = max + 1;
        user.setId(next);
        users.add(user);
        write(users);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> update(Long userId, User update) {
        List<User> users = findAll();
       // User editUser = users.stream()
       //         .filter(u -> u.getId()
      //                  .equals(userId))
   //             .findFirst().orElseThrow(() -> new RuntimeException("User not found"));
        User editUser = null;
        for (User user : users){
            if (Objects.equals(user.getId(), userId)){
                editUser=user;
            }
        }
        if (update.getFirstName().isEmpty() ){
            editUser.setFirstName(editUser.getFirstName());
        }
        else editUser.setFirstName(update.getFirstName());
        if (update.getLastName().isEmpty()){
            editUser.setLastName(editUser.getLastName());
        } else editUser.setLastName(update.getLastName());
        if (update.getPhone().isEmpty()){
            editUser.setPhone(editUser.getPhone());
        }else editUser.setPhone(update.getPhone());
        write(users);
        return Optional.of(update);
    }

    @Override
    public boolean delete(Long id) {
        List<User> users = findAll();
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (Objects.equals(user.getId(), id)) {
                iterator.remove();
                write(users); // Записываем обновленный список пользователей в файл
                return true;
            }
        }
        return false;
    }

    private void write(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u: users) {
            lines.add(mapper.toInput(u));
        }
        saveAll(lines);
    }
    @Override
    public List<String> readAll() {
        List<String> lines = new ArrayList<>();
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    @Override
    public void saveAll(List<String> data) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            for (String line : data) {
                writer.write(line);
                writer.append('\n');
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createDB() {
        try {
            File db = new File(fileName);
            if (db.createNewFile()) {
                System.out.println("DB created");
            } else {
                System.out.println("DB already exists");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
