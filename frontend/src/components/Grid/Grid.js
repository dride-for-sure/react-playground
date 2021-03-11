import styled from 'styled-components';

const Grid = styled.main`
  display:grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  grid-template-rows: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px 20px;
  padding: 20px;
  `;

export default Grid;
