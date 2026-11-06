package in.sp.main.Configuration;

import in.sp.main.Model.User;
import in.sp.main.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

//        Step 1. How to fetch the user from the database.
        User user = userRepository.findByUserEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("could not find exception: "+username);
        }

        return  new CustomUserDetails(user);
    }
}
