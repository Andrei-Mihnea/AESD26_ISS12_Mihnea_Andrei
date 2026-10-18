am creat serviciul de borrow care atunci cand este apelat logging-ul si validarea ruleaza in pipeline
atunci cand este imprumutata o carte este verificat rolul utilizatorului care imprumuta cartea si bazat pe rolul acestuia poate imprumuta mai multe sau mai putine carti
in policy config am setat modul in care este handled policy-ul cum este setat (role based sau fixed limit)
am creat 2 clase repository pentru a obtine informatiile local stocate(in memory)
si am creat un custom Exception care este folosit pentru exceptiile related cu imprumutatul
