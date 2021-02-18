import styled from 'styled-components';

export const Head = styled.header`
  display: flex;
  flex-direction: column;
  background-color: darkseagreen;
  color: white;
  padding: 20px;
  box-sizing: border-box;
  height: 200px;

  > a {
    text-decoration: none;
    color: white;
    text-align: right;

    &:hover {
      opacity: .7;
    }  
  }
`;

export const H1 = styled.h1`
  text-transform: uppercase;
  padding: 0;
  margin: 0;
`;

export const Span = styled.span`
  font-style: italic;
  flex-grow: 2;
`;
