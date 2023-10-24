package basepackage.stand.standbasisprojectonev1.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import basepackage.stand.standbasisprojectonev1.model.RoleName;
import basepackage.stand.standbasisprojectonev1.model.User;
import basepackage.stand.standbasisprojectonev1.payload.Permissions;
import basepackage.stand.standbasisprojectonev1.repository.UserRepository;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Loy by August 2022
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                Long userId = tokenProvider.getUserIdFromJWT(jwt); //this is id not userId
               
                User u = userRepository.findByUserId(userId);
                Gson gsonObj = new Gson();
               
                Type type = new TypeToken<Map<String, Permissions>>(){}.getType();
                Map<String,Permissions> attributes = gsonObj.fromJson(u.getPermissionsJSON(), type );
              //  System.out.println("Attributes delete " + request.getRequestURI() + " " + request.getMethod().toLowerCase()  );
                
                if ( attributes.get("school") != null  ) {
                	System.out.println("Within school.....");
                	Permissions perm = (Permissions) attributes.get("school");                	
                	
                	if ( request.getRequestURI().contains("/api/school") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{      
                		 
                		 System.out.println("This user does not have read permissions for the School data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}

                	if ( request.getRequestURI().contains("/api/school") == true && 
                		 perm.getCreate() != true && 
                		 request.getMethod().toLowerCase().equals("post")	
                	   ) 
                		{ 
                		
                		 System.out.println("This user does not have create permissions for the School data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/school") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")
                   	   ) 
                   		{        
                		
                   		 System.out.println("This user does not have edit permissions for the School data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/school") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{
                		
                      	 System.out.println("This user does not have delete permissions for the School data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("groupofschool") != null  ) {
                	Permissions perm = (Permissions) attributes.get("groupofschool");                	
                	
                	if ( request.getRequestURI().contains("/api/schoolgroup") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{      
                		
                		 System.out.println("This user does not have read permissions for the School Group data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/schoolgroup") == true && 
                   		 perm.getCreate() != true && 
                   		 request.getMethod().toLowerCase().equals("post")	
                   	   ) 
                   		{      
                		
                   		 System.out.println("This user does not have create permissions for the School Group data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/schoolgroup") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")
                   	   ) 
                   		{   
                		
                   		 System.out.println("This user does not have edit permissions for the School Group data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/schoolgroup") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{ 
                		
                      	 System.out.println("This user does not have delete permissions for the School Group data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("user") != null  ) {
                	Permissions perm = (Permissions) attributes.get("user");                	
                	
                	if ( request.getRequestURI().contains("/api/user") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{  
                		
                		 System.out.println("This user does not have read permissions for the User data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/user") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      		{  
                			
                      		 System.out.println("This user does not have create permissions for the User data. ");
                      		filterChain.doFilter(request, response);
                   		 return;
                      		}
                	
                	if ( request.getRequestURI().contains("/api/user") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the User data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/user") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the User data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("teacher") != null  ) {
                	Permissions perm = (Permissions) attributes.get("teacher");                	
                	
                	/*if ( request.getRequestURI().contains("/api/teacher") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Teacher data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		} */ 
                	
                	if ( request.getRequestURI().contains("/api/teacher") == true && 
                     		 perm.getCreate() != true && 
                     		 request.getMethod().toLowerCase().equals("post")	
                     	   ) 
                     	{                		 
                     	 System.out.println("This user does not have create permissions for the Teacher data. ");
                     	filterChain.doFilter(request, response);
               		 return;
                     	}
                	
                	if ( request.getRequestURI().contains("/api/teacher") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Teacher data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/teacher") == true && 
                      		 perm.getEdit() == true && 
                      		 request.getMethod().toLowerCase().equals("put") &&
                      		 u.getRole() == RoleName.TEACHER
                      	   )                 		
                      		{
                				Long id = Long.parseLong( request.getRequestURI().replaceAll(".*/", "") );
	                			if(id != userId){
	                				 System.out.println("This teacher does not have edit permissions for another Teacher's data. ");
		                      		 filterChain.doFilter(request, response);
		                      		 return;
	                			}
	                      		 
                      		}
                	
                	if ( request.getRequestURI().contains("/api/teacher") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Teacher data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("enrollment") != null  ) {
                	Permissions perm = (Permissions) attributes.get("enrollment");                	
                	
                	if ( request.getRequestURI().contains("/api/enrollment") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Enrollment data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/enrollment") == true && 
                    		 perm.getCreate() != true && 
                    		 request.getMethod().toLowerCase().equals("post")	
                    	   ) 
                    	{                		 
                    	 System.out.println("This user does not have create permissions for the Enrollment data. ");
                    	 filterChain.doFilter(request, response);
                		 return;
                    	}
                	
                	if ( request.getRequestURI().contains("/api/enrollment") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Enrollment data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/enrollment") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Enrollment data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("classroom") != null  ) {
                	Permissions perm = (Permissions) attributes.get("classroom");                	
                	
                	if ( request.getRequestURI().contains("/api/class") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Classroom data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/class") == true && 
                   		 perm.getCreate() != true && 
                   		 request.getMethod().toLowerCase().equals("post")	
                   	   ) 
                   	{                		 
                   	 	System.out.println("This user does not have create permissions for the Classroom data. ");
                   	 filterChain.doFilter(request, response);
            		 return;
                   	}
                	
                	if ( request.getRequestURI().contains("/api/class") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Classroom data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/class") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Classroom data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("calendar") != null  ) {
                	Permissions perm = (Permissions) attributes.get("calendar");                	
                	
                	if ( request.getRequestURI().contains("/api/calendar") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")	
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the calendar data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/calendar") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      	{                		 
                      	 	System.out.println("This user does not have create permissions for the calendar data. ");
                      	 	filterChain.doFilter(request, response);
                   		 return;
                      	}
                	
                	if ( request.getRequestURI().contains("/api/calendar") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the calendar data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/calendar") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the calendar data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                 
                if ( attributes.get("timetable") != null  ) {
                	Permissions perm = (Permissions) attributes.get("timetable");                	
                	
                	if ( request.getRequestURI().contains("/api/timetable") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Timetable data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/timetable") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      	{                		 
                      	 	System.out.println("This user does not have create permissions for the Timetable data. ");
                      	 	filterChain.doFilter(request, response);
                   		 return;
                      	}
                	
                	if ( request.getRequestURI().contains("/api/timetable") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Timetable data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/timetable") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Timetable data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("lessonnote") != null  ) {
                	Permissions perm = (Permissions) attributes.get("lessonnote");                	
                	
                	if ( request.getRequestURI().contains("/api/lessonnote") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Lessonnote data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/lessonnote") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      	{                		 
                      	 	System.out.println("This user does not have create permissions for the Lessonnote data. ");
                      	 	filterChain.doFilter(request, response);
                   		 return;
                      	}
                	
                	if ( request.getRequestURI().contains("/api/lessonnote") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Lessonnote data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/lessonnote") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Lessonnote data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("attendance") != null  ) {
                	Permissions perm = (Permissions) attributes.get("attendance");                	
                	System.out.println("Within attendance.....");
                	if ( request.getRequestURI().contains("/api/attendance") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the Attendance data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/attendance") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      	{                		 
                      	 	System.out.println("This user does not have create permissions for the Attendance data. ");
                      	 	filterChain.doFilter(request, response);
                   		 return;
                      	}
                	
                	if ( request.getRequestURI().contains("/api/attendance") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the Attendance data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/attendance") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the Attendance data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                if ( attributes.get("dashboard") != null  ) {
                	Permissions perm = (Permissions) attributes.get("dashboard");                	
                	System.out.println("Within dashboard.....");
                	if ( request.getRequestURI().contains("/api/dashboard") == true && 
                		 perm.getRead() != true && 
                		 request.getMethod().toLowerCase().equals("get")
                	   ) 
                		{                		 
                		 System.out.println("This user does not have read permissions for the dashboard data. ");
                		 filterChain.doFilter(request, response);
                		 return;
                		}
                	
                	if ( request.getRequestURI().contains("/api/dashboard") == true && 
                      		 perm.getCreate() != true && 
                      		 request.getMethod().toLowerCase().equals("post")	
                      	   ) 
                      	{                		 
                      	 	System.out.println("This user does not have create permissions for the dashboard data. ");
                      	 	filterChain.doFilter(request, response);
                   		 return;
                      	}
                	
                	if ( request.getRequestURI().contains("/api/dashboard") == true && 
                   		 perm.getEdit() != true && 
                   		 request.getMethod().toLowerCase().equals("put")	
                   	   ) 
                   		{                		 
                   		 System.out.println("This user does not have edit permissions for the dashboard data. ");
                   		filterChain.doFilter(request, response);
               		 return;
                   		}
                	
                	if ( request.getRequestURI().contains("/api/dashboard") == true && 
                      		 perm.getDelete() != true && 
                      		 request.getMethod().toLowerCase().equals("delete")	
                      	   ) 
                      	{                		 
                      	 System.out.println("This user does not have delete permissions for the dashboard data. ");
                      	filterChain.doFilter(request, response);
               		 return;
                      	}
                }
                
                
                           
                
                /*
                    Note that you could also encode the user's username and roles inside JWT claims
                    and create the UserDetails object by parsing those claims from the JWT.
                 */
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
                return;
            }
            else {
            	System.out.println("JWT Token does not begin with Bearer String");
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
