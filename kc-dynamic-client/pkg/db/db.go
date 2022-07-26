package db

import (
	"database/sql"
	"fmt"
	_ "github.com/lib/pq"
)

const (
	host     = "localhost"
	port     = 5432
	user     = "postgres"
	password = "pkctest123"
	dbname   = "kctest"
)

type connection struct {
	db *sql.DB
}

func NewConnection() *connection {
	psqlconn := fmt.Sprintf("host=%s port=%d user=%s password=%s dbname=%s sslmode=disable", host, port, user, password, dbname)
	db, err := sql.Open("postgres", psqlconn)
	CheckError(err)
	return &connection{
		db: db,
	}
}

func CheckError(err error) {
	if err != nil {
		panic(err)
	}
}

func (d connection) CheckConnection() (sql.Result, error) {
	return d.db.Exec("SELECT 1")
}

func (d connection) Execute(statement string, args ...any) (sql.Result, error) {
	return d.db.Exec(statement, args)
}

func (d connection) Query(q string, args ...any) (*sql.Rows, error) {
	return d.db.Query(q, args)
}

func (d connection) SaveClients(client_id string, token string) (*sql.Rows, error) {
	rows, err := d.db.Query("INSERT INTO clients(client_id, registration_token) VALUES($1, $2);", client_id, token)
	if err != nil {
		fmt.Println(err)
		return nil, err
	}
	return rows, nil
}

func (d connection) GetClients(client_id string) (string, string) {
	var id, token string
	rows := d.db.QueryRow("SELECT * FROM clients WHERE client_id= $1;", client_id).Scan(&id, &token)
	fmt.Println(rows)
	return id, token
}

func (d connection) Close() {
	defer d.db.Close()
}

func (d connection) Delete(client_id string) *sql.Row {
	rows := d.db.QueryRow("DELETE FROM clients where client_id=$1;", client_id)
	fmt.Println(rows)
	return rows
}

// hardcoded
//insertStmt := `insert into "Students"("Name", "Roll") values('John', 1)`
//_, e := db.Exec(insertStmt)
//CheckError(e)
//
//// dynamic
//insertDynStmt := `insert into "Students"("Name", "Roll") values($1, $2)`
//_, e = db.Exec(insertDynStmt, "Jane", 2)
//CheckError(e)
