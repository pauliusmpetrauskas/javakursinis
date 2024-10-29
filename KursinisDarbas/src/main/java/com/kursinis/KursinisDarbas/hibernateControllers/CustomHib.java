package com.kursinis.KursinisDarbas.hibernateControllers;

import com.kursinis.KursinisDarbas.model.Product;
import com.kursinis.KursinisDarbas.model.User;
import com.kursinis.KursinisDarbas.model.Warehouse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class CustomHib extends GenericHib {
    public CustomHib(EntityManagerFactory entityManagerFactory) {
        super(entityManagerFactory);
    }

    public User getUserByCredentials(String login, String password) {
        EntityManager em = null;
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<User> query = cb.createQuery(User.class);
            Root<User> root = query.from(User.class);
            query.select(root).where(cb.and(cb.like(root.get("login"), login), cb.like(root.get("password"), password)));
            Query q;

            q = em.createQuery(query);
            return (User) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

    public void deleteProduct(int id)
    {
        EntityManager em = getEntityManager();
        try{
            em.getTransaction().begin();
            var product = em.find(Product.class, id);
            //Kai turiu objekta, galiu ji nulinkinti

            Warehouse warehouse = product.getWarehouse();
            if(warehouse !=null)
            {
                warehouse.getInStockProducts().remove(product);
                em.merge(warehouse);
            }

            em.remove(product);
            em.getTransaction().commit();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
