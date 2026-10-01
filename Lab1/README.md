- am creeat cele 2 pagini html simpe in folder-ul src/main/webapp

- pagina dinamica pentru welcome a fost generata in interiorul fisierului index.jsp unde in functie de pagina aleasa de catre utilizator este trimis cate page1.html sau page2.html. Acest lucru este realizat in clasa ControllerServlet unde procesez request-ul user-ului si il verific sa fie unul valid (1 sau 2) iar apoi este trimisa pagina
- pentru a putea obtine informatiile celui care face request-ul am folosit Filter care ma ajuta sa adaug un pas in pipeline-ul aplicatiei pana sa ajunga la controller care logheaza informatiile dupa fiecare request facut de un user in aplicatie
- pentru partea de client am folosit un client python care afiseaza numarul paginii accesate daca este accesat cu succes sau eroare in cazul in care este un bad request
