package com.tictactoe;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Field field = extractField(session);
        int index = getSelectedIndex(req);

        Sign sign = field.getField().get(index);
        if (Sign.EMPTY != sign) {
            getServletContext().getRequestDispatcher("/index.jsp").forward(req, resp);
            return;
        }

        field.getField().put(index, Sign.CROSS);
        if (checkWin(resp, session, field)) {
            return;
        }

        int emptyIndex = field.getEmptyFieldIndex();
        if (emptyIndex >= 0) {
            field.getField().put(emptyIndex, Sign.NOUGHT);
            if (checkWin(resp, session, field)) {
                return;
            }
        } else {
            session.setAttribute("draw", true);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            resp.sendRedirect("/index.jsp");
            return;
        }
        List<Sign> data = field.getFieldData();

        session.setAttribute("data", data);
        session.setAttribute("field", field);

        resp.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        return click.matches("\\d+") ? Integer.parseInt(click) : 0;
        // boolean isNumber = click.chars().allMatch(Character::isDigit);
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session aborted. Try again!");
        }
        return (Field) field;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winnerSign = field.checkWin();
        if (Sign.CROSS == winnerSign || Sign.NOUGHT == winnerSign) {
            session.setAttribute("winner", winnerSign);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            response.sendRedirect("/index.jsp");
            return true;
        }
        return false;
    }
}
