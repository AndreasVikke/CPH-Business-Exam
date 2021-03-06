
package utils;

import entities.DayPlan;
import entities.MenuPlan;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Andreas Vikke
 */
public class SetupTestMenuPlan {
    public static void main(String[] args) {
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
        EntityManager em = emf.createEntityManager();
        
        User user = em.find(User.class, "user");
        List<DayPlan> dayPlans = new ArrayList();
        MenuPlan menuPlan = new MenuPlan(user, 1, dayPlans);
        
        dayPlans.add(new DayPlan("slow cooker beef stew", 1, menuPlan));
        dayPlans.add(new DayPlan("Smoked paprika goulash for the slow cooker", 2, menuPlan));
        dayPlans.add(new DayPlan("Moist garlic roasted chicken", 3, menuPlan));
        dayPlans.add(new DayPlan("Polly's eccles cakes", 4, menuPlan));
        dayPlans.add(new DayPlan("Braised beef in red wine", 5, menuPlan));
        dayPlans.add(new DayPlan("Tofu vindaloo", 6, menuPlan));
        dayPlans.add(new DayPlan("Pistachio chicken with pomegranate sauce", 7, menuPlan));

        em.getTransaction().begin();
        em.persist(menuPlan);
        for(DayPlan dayPlan : dayPlans)
            em.persist(dayPlan);
        em.getTransaction().commit();
    }
}
