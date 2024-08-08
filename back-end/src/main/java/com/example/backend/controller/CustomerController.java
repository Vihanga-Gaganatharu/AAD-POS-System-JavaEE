package com.example.backend.controller;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

import com.example.backend.bo.BOFactory;
import com.example.backend.bo.custom.CustomerBo;
import com.example.backend.bo.custom.CustomerBoImpl;
import com.example.backend.dto.CustomerDto;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet( urlPatterns = "/customer" )
public class CustomerController extends HttpServlet {

    CustomerBo customerBo = (CustomerBo) BOFactory.getBoFactory().getBO(BOFactory.BOTypes.CUSTOMER);

    // TODO : Add Customer
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try ( var reader = req.getReader(); var writer = resp.getWriter()) {

            Jsonb jsonb = JsonbBuilder.create();
            CustomerDto customerDto = jsonb.fromJson(reader, CustomerDto.class);

           try {
               if (customerBo.addCustomer(customerDto)){
                     resp.setStatus(HttpServletResponse.SC_CREATED);
                     writer.write("Customer Added Successfully");
               }
               else {
                   resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                   writer.write("Failed to add Customer");
               }
           } catch (SQLException e) {
                e.printStackTrace();
           }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // TODO : Get Customer
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try{

            if (req.getParameter("id").equals("all")){
                List<CustomerDto> allCustomers = customerBo.getAllCustomers();
                if (allCustomers != null){
                    resp.setContentType("application/json");
                    Jsonb jsonb = JsonbBuilder.create();
                    jsonb.toJson(allCustomers, resp.getWriter());
                }
                return;
            }


            String id = req.getParameter("id");
            CustomerDto customerDto = customerBo.searchCustomer(id);
            if (customerDto != null){
                resp.setContentType("application/json");
                Jsonb jsonb = JsonbBuilder.create();
                jsonb.toJson(customerDto, resp.getWriter());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    //Todo : Update Customer
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if (req.getContentType() == null || !req.getContentType().toLowerCase().startsWith("application/json")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try (var reader = req.getReader(); var writer = resp.getWriter()) {

            Jsonb jsonb = JsonbBuilder.create();
            CustomerDto customerDto = jsonb.fromJson(reader, CustomerDto.class);

            if (customerBo.updateCustomer(customerDto)) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                writer.write("Customer Updated Successfully");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write("Failed to update Customer");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String id = req.getParameter("id");

        try {
            if (customerBo.deleteCustomer(id)){
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write("Customer Deleted Successfully");
            }
            else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Failed to delete Customer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void destroy() {
        super.destroy();
    }
}